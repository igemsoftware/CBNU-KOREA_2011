package mgBrowser07;
/**
 * Class to load an EMBL or Genbank sequence file and display it in a viewer.
 */
 
//Java libraries
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
//BioJava libraries
import org.biojava.bio.*;
import org.biojava.bio.seq.*;
import org.biojava.bio.gui.sequence.*;
//BioJava extension libraries
import org.biojavax.*;
import org.biojavax.ontology.*;
import org.biojavax.bio.seq.*;
 
public class LinearMap extends JFrame implements SequenceViewerMotionListener {

  private static final long serialVersionUID = 1L;

  private TranslatedSequencePanel tsp = new TranslatedSequencePanel();
  private MultiLineRenderer mlr = new MultiLineRenderer();
  private RulerRenderer rr = new RulerRenderer();
  private SequenceRenderer sr = new SymbolSequenceRenderer();
  private FeatureBlockSequenceRenderer fwd_fbsr;
  private FeatureBlockSequenceRenderer rev_fbsr;
  private RichSequence richSeq;
  
  private int seqLen;
  private String locus;
  String circular;
  
  private Container con;
  private JPanel jPanel;
  private JButton mvLeft, mvRight, zoomIn, zoomOut;
  private double sScale = 0.05;
  private int wWidth = 584;    //원래 1200  확정
  private int wHeight = 200;
 
  public LinearMap(String fileName){
    super("Linear Genome Browser");
    
    try {
      //richSeq = RichSequence.IOTools.readEMBLDNA(new BufferedReader(new FileReader(new File(fileName))), null).nextRichSequence();//Load the sequence file
    	richSeq = RichSequence.IOTools.readGenbankDNA(new BufferedReader(new FileReader(new File(fileName))), null).nextRichSequence();
    }
    catch (BioException bioe){
      System.err.println("Not an GenBank sequence");
    }
    catch(FileNotFoundException fnfe){
       System.err.println("FileNotFoundException: " + fnfe);
    }
    catch (IOException ioe){
      System.err.println("IOException: " + ioe);
    }

    locus = richSeq.getName();
	if(richSeq.getCircular()) circular = "circular";  
	seqLen = richSeq.length();  

    //Define the appearance of the rendered Features
    BasicFeatureRenderer fwd_bfr = new BasicFeatureRenderer();
    GradientPaint fwd_gradient = new GradientPaint(0, 10, Color.BLUE, 0, 0, Color.white, true);
    fwd_bfr.setFill(fwd_gradient);
    fwd_bfr.setOutline(Color.BLUE);
    
    BasicFeatureRenderer rev_bfr = new BasicFeatureRenderer();
    GradientPaint rev_gradient = new GradientPaint(0, 10, Color.RED, 0, 0, Color.white, true);
    rev_bfr.setFill(rev_gradient);
    rev_bfr.setOutline(Color.RED);
    
    
 
    //Form a bridge between Sequence rendering and Feature rendering
    fwd_fbsr = new FeatureBlockSequenceRenderer(fwd_bfr);
    fwd_fbsr.setCollapsing(false);
 
    //Filter for CDS features on the forward strand
    SequenceRenderer fwd_sr = new FilteringRenderer(fwd_fbsr,
            new FeatureFilter.And(new FeatureFilter.ByType("gene"),            //원래는 "CDS"
            new FeatureFilter.StrandFilter(StrandedFeature.POSITIVE)),
            true);
    
    //Form a bridge between Sequence rendering and Feature rendering
    rev_fbsr = new FeatureBlockSequenceRenderer(rev_bfr);
    rev_fbsr.setCollapsing(false);
    
    //Filter for CDS features on the reverse strand
    SequenceRenderer rev_sr = new FilteringRenderer(rev_fbsr,
            new FeatureFilter.And(new FeatureFilter.ByType("gene"),            //원래는 "CDS"   
            new FeatureFilter.StrandFilter(StrandedFeature.NEGATIVE)),
            true);
 
    //Add the renderers to the MultiLineRenderer
    mlr.addRenderer(fwd_sr);
    mlr.addRenderer(rr);
    mlr.addRenderer(rev_sr);
    mlr.addRenderer(sr);
 
    //Set the sequence renderer for the TranslatedSequencePanel
    tsp.setRenderer(mlr);
    //Set the sequence to render
    tsp.setSequence(richSeq);
    //Set the position of the displayed sequence
    tsp.setSymbolTranslation(1);
    //Set the scale as pixels per Symbol.
    tsp.setScale(sScale);
 
    //Add a sequence viewer motion listener to the TranslatedSequencePanel
    tsp.addSequenceViewerMotionListener(this);
 
    //Generate the control panel
    jPanel = new JPanel();
    jPanel.setBackground(Color.lightGray);
    //Move along the sequence towards 5' end
    mvLeft = new JButton("<<");
    mvLeft.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent ae){
        int rightSide = tsp.getRange().getMax();
        int leftSide = tsp.getRange().getMin();
        int newStartPoint = leftSide - (rightSide - leftSide);
        if (newStartPoint < 1){
          newStartPoint = 1;
        }
        tsp.setSymbolTranslation(newStartPoint);
      }
    });
    //Move along the sequence towards 3' end
    mvRight = new JButton(">>");
    mvRight.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent ae){
        int rightSide = tsp.getRange().getMax();
        int leftSide = tsp.getRange().getMin();
        int screenWidth = rightSide - leftSide;
        if ((rightSide + screenWidth) >= richSeq.length()){
          tsp.setSymbolTranslation(richSeq.length() - screenWidth);
        }
        else {
          tsp.setSymbolTranslation(rightSide);
        }
      }
    });
    //Increase sequence scale
    zoomIn = new JButton("+");
    zoomIn.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent ae){
        sScale = sScale * 2;
        //if sequence scale = 12 the bases are rendered
        //no need to zoom in further so disable the button.
        if (sScale > 12){
          sScale = 12;
          zoomIn.setEnabled(false);
        }
        tsp.setScale(sScale);
      }
    });
    //Reduce sequence scale
    zoomOut = new JButton("-");
    zoomOut.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent ae){
        sScale = sScale / 2;
        //if sequence scale is below 12 the enable zoomIn button
        if (sScale < 12){
          zoomIn.setEnabled(true);
        }
        //If the scale allows more than the sequence to be displayed
        //display the whole sequence
        if (sScale < ((double)tsp.getWidth()/(double)richSeq.length())){
          sScale = (double)tsp.getWidth()/(double)richSeq.length();
          tsp.setSymbolTranslation(1);
        }
        tsp.setScale(sScale);
        //If the new scale coupled with the current SymbolTranslation means the
        //displayed sequence can't fill the TranslatedSequencePanel then reset 
        //the SymbolTranlstion to allow for this
        if(tsp.getRange().getMax() >= richSeq.length()){
          int tmp = (int)((double)tsp.getWidth()/sScale);
          tsp.setSymbolTranslation(richSeq.length() - tmp);
        }
      }
    });
    jPanel.add(mvLeft);
    jPanel.add(mvRight);
    jPanel.add(zoomIn);
    jPanel.add(zoomOut);
 
    con = new Container();
    con = getContentPane();
    con.setLayout(new BorderLayout());
    con.add(jPanel, BorderLayout.NORTH);
    con.add(tsp, BorderLayout.CENTER);
    setLocation(0,605);          //linear genome browser의 위치
    setSize(wWidth,wHeight);     //linear genome browser의 크기
    setVisible(true);
    setResizable(false);
    
  }
 
	public int getSeqLen() {                          // 서열 길이 반환
		return seqLen;
	}
	
	public String getLocus() {
		return locus;
	}
  
  /**
   * Detect mouse dragged events
   * @param sve
   */
  public void mouseDragged(SequenceViewerEvent sve) {
  }
 
  /**
   * Detect mouse mouse moved events
   * If the mouse moves over a CDS feature create a tooltiptext stating the
   * the name of the gene associated with the CDS feature.
   * @param sve
   */
  public void mouseMoved(SequenceViewerEvent sve) {
    //Manage the tooltip
    ToolTipManager ttm = ToolTipManager.sharedInstance();
    ttm.setDismissDelay(2000);
    //If the mouse have moved over a SimpleFeatureHolder
    if (sve.getTarget() instanceof SimpleFeatureHolder){
      ComparableTerm gene = RichObjectFactory.getDefaultOntology().getOrCreateTerm("gene");
      SimpleFeatureHolder sfh = (SimpleFeatureHolder)sve.getTarget();
      FeatureHolder fh = sfh.filter(new FeatureFilter.ByType("gene"));           //원래는 "CDS"
      //Iterator <RichFeature> i =  fh.features();
      Iterator <Feature> i =  fh.features();
      while(i.hasNext()){
        RichFeature rf = (RichFeature) i.next();
        RichAnnotation anno = (RichAnnotation) rf.getAnnotation();
        Set annotationNotes = anno.getNoteSet();
        for (Iterator <Note> it = annotationNotes.iterator(); it.hasNext();) {
          Note note = it.next();
          if (note.getTerm().equals(gene)) {
            tsp.setToolTipText("Gene: " + note.getValue());
          }
        }
      }
    }
    else {
      //Remove the tooltip
      ttm.setDismissDelay(10);
    }
  }

  /**
   * Overridden so program terminates when window closes
   */
  protected void processWindowEvent(WindowEvent we){
    if (we.getID() == WindowEvent.WINDOW_CLOSING) {
      //System.exit(0);
    	dispose();
    }
    else {
      super.processWindowEvent(we);
    }
  }
  
  
  /**
   * Main method
   * @param args
   */
  public static void main(String args []){
    if (args.length == 1){
      new LinearMap(args[0]);
    }
    else {
      System.out.println("Usage: java SequenceViewer <GenBank file>");
      //System.exit(1);
    }
  }
}
