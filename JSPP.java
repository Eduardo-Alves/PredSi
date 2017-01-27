import java.io.*;
import jspp.*;


public class JSPP {
  public JSPP() {
  }


  public static void main(String[] args) {
    if(args.length!=3){
      System.out.println(
          "USAGE: JSPP positive-matrix.smx sequences.fasta result.txt");
      System.exit(0);
    }
    File pmatrix=new File(args[0]);

    File sequences=new File(args[1]);
    File result=new File(args[2]);
    try {
      //import matrix
      BufferedReader se = new BufferedReader(new FileReader(sequences));
      ObjectInputStream in = new ObjectInputStream(new FileInputStream(pmatrix));
      SearchMatrix smp = (SearchMatrix) in.readObject();
      in.close();

      //construct a SignalPeptidePredictor object
      SignalPeptidePredictor pd = new SignalPeptidePredictor(smp);
      BufferedWriter out = new BufferedWriter(new FileWriter(result));
      out.write("##gff-version 2\n");
      out.write("##sequence-name source  feature start   end     score   N/A ?\n");
      String line;
      while ( (line = se.readLine()) != null) {
        if (line.indexOf(">") == -1) {
          continue;
        }
        String id = line.substring(line.indexOf(">") + 1);
        String sequence;
        if ( (sequence = se.readLine()) == null) {
          System.err.println("Error reading FASTA-File !");
          System.exit(0);
        }

        int cpos = pd.predictEnhancedPosition(sequence);
        double score=pd.getScore();
        String yn;
        if (pd.isSignalPeptide()) {
        out.write(id + "\tPredSi\tSIGNAL\t1\t" + cpos + "\t" + score +  "\t.\t.\tYES\n");
        }
      }
      se.close();
      out.close();
    }
    catch (IOException ex) {
      System.err.println("IO-Error: "+ex);
    }
    catch (ClassNotFoundException ex) {
      System.err.println("Error reading matrices (wrong file ?)"+ex);
    }


  }

}

