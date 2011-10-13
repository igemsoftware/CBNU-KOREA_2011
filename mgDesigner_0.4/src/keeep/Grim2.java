package keeep;
import java.awt.*; 
import javax.swing.*; 
import java.awt.event.*; 
import java.util.*; 
import java.awt.geom.*; 

public class Grim2 extends JFrame implements ActionListener, MouseListener,MouseMotionListener { 
    private JMenuBar jmb; 
    private JMenu fileJMenu, windowJMenu; 
    private JMenuItem newFileJMenuItem, openFileJMenuItem, saveFileJMenuItem, exitJMenuItem;                               
    private JCheckBoxMenuItem toolJMenuItem, infoJMenuItem, colorButtonJMenuItem,  
                                            colorScrollJMenuItem, gradationJMenuItem; 
    private JButton pencilJButton, straightJButton, rectJButton, circleJButton, circleRectJButton, 
                          magnifierJButton, eraserJButton; 
    private JPanel toolButtonJPanel, tooldownButtonJPanel, toolWestJPanel, lineWidthJPanel, checkBoxPanel, scaleJPanel, mainJPanel; 
    private JLabel pointJLabel, rgbJLabel, lineWidthJLabel, scaleJLabel; 
    private JComboBox lineWidthJComboBox, scaleJComboBox; 
    private String pointStr, rgbStr;  
    private String lineWidthStr[] = {"1.0","2.0","3.0","4.0","5.0"}; 
    private String scaleStr[] = {"1.0","2.0","3.0","4.0","0.5"}; 
    private Graphics2D g; 
    private Color color; 
    private JCheckBox jumLineJCheckBox, fillJCheckBox; 
    private int mousePointX, mousePointY, mousePointXX, mousePointYY, pressMousePointX,pressMousePointY, redColor, greenColor, blueColor; 
    //100 : 연필, 101 : 직선, 102: 사각형, 103 : 원, 104 : 둥근사각, 105 : 돋보기, 106 : 지우개 
    private int selectButton=100, lineWidth=1; 
    private double scale = 1.0; 
    private boolean isMousePress = false, isDot = false, isFill=false; 
    private Vector  dataVector  =  new  Vector(10,5); 
    private JToolBar toolJToolBar,infoJToolBar; 
    private float[] dash={5.0f,5.0f,5.0f,5.0f};       

    public Grim2(){ 
         
        super("그림판"); 
        setBounds(500,200,500,600); 
        setVisible(true); 
        setJMenuBar(setMenu()); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE ); 
        mainJPanel = new JPanel(); 
        mainJPanel.setBackground(Color.white); 
        color = new Color(0,0,0); 


        //선 굵기 패널 생성 
        lineWidthJPanel = new JPanel(); 
        lineWidthJLabel = new JLabel("선굵기"); 
        lineWidthJComboBox = new JComboBox(lineWidthStr); 
        lineWidthJComboBox.setSelectedIndex(lineWidth-1); 
        lineWidthJPanel.add(lineWidthJLabel); 
        lineWidthJPanel.add(lineWidthJComboBox); 

        //돋보기 패널 생성 
        scaleJPanel = new JPanel(); 
        scaleJLabel = new JLabel("화면크기"); 
        scaleJComboBox = new JComboBox(scaleStr); 
        scaleJComboBox.setSelectedIndex(lineWidth-1); 
        scaleJPanel.add(scaleJComboBox); 

        //체크박스 및 아이템 설정 
        checkBoxPanel = new JPanel(); 
        jumLineJCheckBox = new JCheckBox("점선"); 
        fillJCheckBox = new JCheckBox("fill"); 
        checkBoxPanel.add(jumLineJCheckBox); 
        checkBoxPanel.add(fillJCheckBox); 


        getContentPane().add(mainJPanel,"Center"); 
        toolWest(); 
        lineWidthJComboBox.addActionListener(this); 
        jumLineJCheckBox.addActionListener(this); 
        fillJCheckBox.addActionListener(this); 
        scaleJComboBox.addActionListener(this); 
        addMouseListener(this); 
        addMouseMotionListener(this); 
    } 
     
    public void setPointStr(){ 
        pointStr = "x : " + mousePointX + ", y : " + mousePointY; 
    } 

    public void setRgbStr(){ 
        rgbStr = "r : " + redColor + " g : " + greenColor + " b : " + blueColor; 
    } 


    public void toolWest(){ 
        toolWestJPanel = new JPanel(new BorderLayout()); 

        //toolJToolBar 설정 
        toolJToolBar = new JToolBar("도구"); 
        toolJToolBar.setVisible(true); 
        toolJToolBar.setLayout(new BorderLayout()); 


        //infoJToolBar 설정 
        infoJToolBar = new JToolBar("정보"); 
        infoJToolBar.setLayout(new GridLayout(2,1,2,15)); 
        setPointStr(); 
        setRgbStr(); 
        pointJLabel = new JLabel(pointStr); 
        rgbJLabel = new JLabel(rgbStr); 
        infoJToolBar.setVisible(true); 
        infoJToolBar.add(pointJLabel); 
        infoJToolBar.add(rgbJLabel); 
         
        toolWestJPanel.add(toolJToolBar,"Center"); 
        toolWestJPanel.add(infoJToolBar,"South"); 
         

        //버튼 및 패널 생성 
        toolButtonJPanel = new JPanel(new GridLayout(7,1)); 
        pencilJButton = new JButton("연필"); 
        straightJButton = new JButton("직선"); 
        rectJButton = new JButton("네모"); 
        circleJButton = new JButton("원"); 
        circleRectJButton = new JButton("둥근네모"); 
        magnifierJButton = new JButton("돋보기"); 
        eraserJButton = new JButton("지우개"); 
         
        //버튼 패널에 add 
        toolButtonJPanel.add(pencilJButton); 
        toolButtonJPanel.add(straightJButton); 
        toolButtonJPanel.add(rectJButton); 
        toolButtonJPanel.add(circleJButton); 
        toolButtonJPanel.add(circleRectJButton); 
        toolButtonJPanel.add(magnifierJButton); 
        toolButtonJPanel.add(eraserJButton); 

        //버튼에 이벤트 add 
        pencilJButton.addActionListener(this); 
        straightJButton.addActionListener(this); 
        rectJButton.addActionListener(this); 
        circleJButton.addActionListener(this); 
        circleRectJButton.addActionListener(this); 
        magnifierJButton.addActionListener(this); 
        eraserJButton.addActionListener(this); 


        //버튼 밑 부분 패널 생성 
        tooldownButtonJPanel = new JPanel(new GridLayout(3,1)); 


        //패널 프레임에 add 
        toolJToolBar.add(toolButtonJPanel,"North");         
        toolJToolBar.add(tooldownButtonJPanel,"Center"); 
        getContentPane().add(toolWestJPanel,"West"); 
    } 

    //ActionEvent 
    public void actionPerformed(ActionEvent e) { 
        if(e.getSource() == pencilJButton){ 
            selectButton = 100; 
            tooldownButtonJPanel.removeAll(); 
     
        }else if(e.getSource() == straightJButton){ 
            selectButton = 101; 
            tooldownButtonJPanel.removeAll(); 
            lineWidthJComboBox.setSelectedIndex(lineWidth-1); 
            lineWidthJPanel.add(lineWidthJComboBox); 
            tooldownButtonJPanel.add(lineWidthJPanel); 

        }else if(e.getSource() == rectJButton){ 
            selectButton = 102; 
            tooldownButtonJPanel.removeAll(); 
            lineWidthJComboBox.setSelectedIndex(lineWidth-1); 
            lineWidthJPanel.add(lineWidthJComboBox); 
            tooldownButtonJPanel.add(lineWidthJPanel); 
            tooldownButtonJPanel.add(checkBoxPanel); 
        }else if(e.getSource() == circleJButton){ 
            tooldownButtonJPanel.removeAll(); 
            lineWidthJComboBox.setSelectedIndex(lineWidth-1); 
            lineWidthJPanel.add(lineWidthJComboBox); 
            tooldownButtonJPanel.add(lineWidthJPanel); 
            tooldownButtonJPanel.add(checkBoxPanel); 
            selectButton = 103; 
        }else if(e.getSource() == circleRectJButton){ 
            selectButton = 104; 
        }else if(e.getSource() == magnifierJButton){ 
            selectButton = 105; 
            tooldownButtonJPanel.removeAll(); 
            tooldownButtonJPanel.add(scaleJPanel); 
            g.scale(scale,scale); 
        }else if(e.getSource() == eraserJButton){ 
            selectButton = 106; 
        }else if(e.getSource() ==lineWidthJComboBox){ 
            if(((String)lineWidthJComboBox.getSelectedItem()).equals("1.0")){ 
                lineWidth = 1; 
            }else if(((String)lineWidthJComboBox.getSelectedItem()).equals("2.0")){ 
                lineWidth = 2; 
            }else if(((String)lineWidthJComboBox.getSelectedItem()).equals("3.0")){ 
                lineWidth = 3; 
            }else if(((String)lineWidthJComboBox.getSelectedItem()).equals("4.0")){ 
                lineWidth = 4; 
            }else if(((String)lineWidthJComboBox.getSelectedItem()).equals("5.0")){ 
                lineWidth = 5; 
            }         
        }else if(e.getSource() == jumLineJCheckBox){ 
            if(jumLineJCheckBox.isSelected()) 
                isDot = true; 
            else 
                isDot = false; 
        }else if(e.getSource() == fillJCheckBox){ 
            if(fillJCheckBox.isSelected()) 
                isFill = true; 
            else 
                isFill = false; 
        }else if(e.getSource() ==scaleJComboBox){ 
            if(((String)scaleJComboBox.getSelectedItem()).equals("1.0")){ 
                scale = 1.0; 
            }else if(((String)scaleJComboBox.getSelectedItem()).equals("2.0")){ 
                scale = 2.0; 
            }else if(((String)scaleJComboBox.getSelectedItem()).equals("3.0")){ 
                scale = 3.0; 
            }else if(((String)scaleJComboBox.getSelectedItem()).equals("4.0")){ 
                scale = 4.0; 
            }else if(((String)scaleJComboBox.getSelectedItem()).equals("0.5")){ 
                scale = 0.5; 
            } 
            g.scale(scale,scale); 
        } 
        repaint(); 
    } 

    class  drawData{ 
        int  mousePointX,  mousePointY; 
        int  mousePointXX,  mousePointYY; 
        int  selectButton;   
        int lineWidth; 
        Color color; 
        boolean isDot=false; 
        boolean isFill=false; 
    } 

     
    public void drawPencil(int x1, int y1, int x2, int y2){ 
        if(isMousePress == true){ 
            drawData  data=new  drawData(); 
            data.mousePointX=x1; 
            data.mousePointY=y1; 
            data.mousePointXX=x2; 
            data.mousePointYY=y2; 
            data.lineWidth=lineWidth; 
            data.color=color; 
            data.selectButton=100; 
            //g = (Graphics2D)mainJPanel.getGraphics(); 
            g.setColor(color); 
            if(isDot == false){ 
                g.setStroke(new BasicStroke(lineWidth)); 
            }else{                 
                g.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER,1.0f,dash, 0)); 
                data.isDot = isDot; 
            } 
            g.drawLine(x1,y1,x2,y2); 
            dataVector.addElement(data); 
        } 
    } 

    public void drawStraight(int x1, int y1, int x2, int y2, int x3, int y3){ 
        if(isMousePress == true){ 
            //g = (Graphics2D)mainJPanel.getGraphics(); 
            Line2D l=new Line2D.Double(); 
            GeneralPath gp=new GeneralPath(); 
            g.setXORMode(Color.white); 
            g.setColor(color); 
            if(isDot == false){ 
                g.setStroke(new BasicStroke(lineWidth)); 
            }else{ 
                g.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER,1.0f,dash, 0)); 
            } 

            //g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER , 0.0f)); 

            gp.moveTo(x1,y1); 
            gp.lineTo(x3,y3);  
            g.draw(gp); 
            gp.reset(); 

            gp.moveTo(x1,y1); 
            gp.lineTo(x2,y2);              
            g.draw(gp); 
        } 
    } 
    public void drawRect(int x1, int y1, int x2, int y2, int x3, int y3){ 
        int oldWidth = 0, oldHeight=0; 
        int width = 0, height=0; 
        if(isMousePress == true){ 
            //g = (Graphics2D)mainJPanel.getGraphics(); 
            GeneralPath gp=new GeneralPath();  // GeneralPath 객체를 만든다.             

            g.setColor(color); 
            g.setXORMode(Color.white); 

            if(isDot == false){ 
                g.setStroke(new BasicStroke(lineWidth)); 
            }else{                 
                g.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER,1.0f,dash, 0)); 
            } 
            if(isFill){ 
                gp.moveTo(x1,y1);  
                gp.lineTo(x3,y1);    
                gp.lineTo(x3,y3);    
                gp.lineTo(x1,y3);    
                gp.closePath();       
                g.fill(gp); 

                gp.reset(); 

                gp.moveTo(x1,y1); 
                gp.lineTo(x2,y1);   
                gp.lineTo(x2,y2);   
                gp.lineTo(x1,y2);   
                gp.closePath();      
                g.fill(gp); 
            }else{ 
                gp.moveTo(x1,y1);  
                gp.lineTo(x3,y1);    
                gp.lineTo(x3,y3);    
                gp.lineTo(x1,y3);    
                gp.closePath();       
                g.draw(gp); 

                gp.reset(); 

                gp.moveTo(x1,y1); 
                gp.lineTo(x2,y1);   
                gp.lineTo(x2,y2);   
                gp.lineTo(x1,y2);   
                gp.closePath();      
                g.draw(gp); 
            } 
        } 
    } 

    public void drawCircle(int x1, int y1, int x2, int y2, int x3, int y3){ 
        int oldWidth = 0, oldHeight=0; 
        int width = 0, height=0; 
        if(isMousePress == true){ 
            //g = (Graphics2D)mainJPanel.getGraphics();             
            g.setColor(color); 
            if(isDot == false){ 
                g.setStroke(new BasicStroke(lineWidth)); 
            }else{                 
                g.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER,1.0f,dash, 0)); 
            } 

            oldWidth  =  x3  -  x1; 
            oldHeight  =  y3  -  y1; 

            width  =  x2  -  x1; 
            height  =  y2  -  y1; 

            if(isFill){ 
                g.fillOval (x1,  y1,  oldWidth,  oldHeight); 
                g.fillOval(x1,  y1,  width,  height);     
            }else{ 
                g.setXORMode(Color.white); 
                g.drawOval (x1,  y1,  oldWidth,  oldHeight); 
                g.drawOval(x1,  y1,  width,  height);     
            } 
        } 
    } 

      public  void  paint(Graphics  gg){ 
         // this.g = (Graphics2D)gg; 
          g = (Graphics2D)getGraphics(); 
          GeneralPath gp=new GeneralPath(); 
          drawData  data; 
          int  w,  h; 
            for(int i=0  ;  i<dataVector.size();  i++){ 
                data  =  (drawData)dataVector.elementAt(i); 
                if(data.selectButton==101 || data.selectButton == 100){ 
                    g.setColor(data.color); 
                    if(data.isDot) 
                        g.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER,1.0f,dash, 0)); 
                    else 
                        g.setStroke(new BasicStroke(data.lineWidth)); 
                    g.drawLine(data.mousePointX,  data.mousePointY,data.mousePointXX,data.mousePointYY); 
                }else if(data.selectButton==102){ 
                    w=  data.mousePointXX  -  data.mousePointX; 
                    h=  data.mousePointYY  -  data.mousePointY; 
                    g.setColor(data.color); 
                    if(data.isDot) 
                        g.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER,1.0f,dash, 0)); 
                    else 
                        g.setStroke(new BasicStroke(data.lineWidth)); 
                     
                    gp.moveTo(data.mousePointX,data.mousePointY); 
                    gp.lineTo(data.mousePointXX,data.mousePointY);   
                    gp.lineTo(data.mousePointXX,data.mousePointYY);   
                    gp.lineTo(data.mousePointX,data.mousePointYY);   
                    gp.closePath();      

                    if(data.isFill) 
                        g.fill(gp); 
                    else                         
                        g.draw(gp); 

                    gp.reset();  
                }else if(data.selectButton==103){ 
                    w=  data.mousePointXX  -  data.mousePointX; 
                    h=  data.mousePointYY  -  data.mousePointY; 
                    g.setColor(data.color); 
                    if(data.isDot) 
                        g.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER,1.0f,dash, 0)); 
                    else 
                        g.setStroke(new BasicStroke(data.lineWidth)); 

                    if(data.isFill) 
                        g.fillOval(data.mousePointX,  data.mousePointY,w,h); 
                    else 
                        g.drawOval(data.mousePointX,  data.mousePointY,w,h); 
                } 
            } 
        } 

    //MouseListener 
    public void mouseClicked(MouseEvent e){} 
    public void mouseEntered(MouseEvent e){ 
        setCursor(new Cursor(Cursor.HAND_CURSOR )); 
    } 
    public void mouseExited(MouseEvent e){} 
    public void mousePressed(MouseEvent e){ 
        mousePointX = e.getX(); 
        mousePointY = e.getY(); 
        pressMousePointX = e.getX(); 
        pressMousePointY = e.getY(); 
        isMousePress = true; 
    } 
    public void mouseReleased(MouseEvent e){ 
         drawData  data=new  drawData(); 
         data.mousePointX=pressMousePointX; 
        data.mousePointY=pressMousePointY; 
        data.mousePointXX=e.getX(); 
        data.mousePointYY=e.getY(); 
        data.lineWidth=lineWidth; 
        data.color=color; 
        data.isDot = isDot; 
        data.isFill = isFill; 
        if(selectButton == 101){             
            data.selectButton=101; 
        }else if(selectButton == 102){ 
            data.selectButton=102; 
        }else if(selectButton == 103){ 
            data.selectButton=103; 
        } 
        dataVector.addElement(data); 
        isMousePress = false; 
        repaint(); 
    } 
     

    //MouseMotionListener 
    public void mouseDragged(MouseEvent e){ 
            mousePointXX = mousePointX; 
            mousePointYY = mousePointY; 
            mousePointX = e.getX(); 
            mousePointY = e.getY(); 
        if(selectButton == 100){ 
            drawPencil(mousePointXX, mousePointYY,mousePointX,mousePointY); 
        }else if(selectButton == 101){     
            drawStraight(pressMousePointX,pressMousePointY,mousePointX,mousePointY,mousePointXX,mousePointYY); 
        }else if(selectButton == 102){     
            drawRect(pressMousePointX,pressMousePointY,mousePointX,mousePointY,mousePointXX,mousePointYY); 
        }else if(selectButton == 103){     
            drawCircle(pressMousePointX,pressMousePointY,mousePointX,mousePointY,mousePointXX,mousePointYY); 
        } 
    }     
    public void mouseMoved(MouseEvent e){ 
        mousePointXX = mousePointX; 
        mousePointYY = mousePointY; 
        mousePointX = e.getX(); 
        mousePointY = e.getY(); 
        setPointStr(); 
        setRgbStr(); 
        pointJLabel.setText(pointStr); 
        rgbJLabel.setText(rgbStr);     
    } 

    public JMenuBar setMenu(){ 
        jmb = new JMenuBar(); 

        fileJMenu = new JMenu("파일"); 
        newFileJMenuItem = new JMenuItem("새파일"); 
        openFileJMenuItem = new JMenuItem("열기"); 
        saveFileJMenuItem = new JMenuItem("저장"); 
        exitJMenuItem = new JMenuItem("끝내기"); 
        fileJMenu.add(newFileJMenuItem); 
        fileJMenu.add(openFileJMenuItem); 
        fileJMenu.add(saveFileJMenuItem); 
        fileJMenu.addSeparator(); 
        fileJMenu.add(exitJMenuItem); 


        windowJMenu = new JMenu("윈도우"); 
        toolJMenuItem = new JCheckBoxMenuItem("도구"); 
        infoJMenuItem = new JCheckBoxMenuItem("정보"); 
        colorButtonJMenuItem = new JCheckBoxMenuItem("색상버튼"); 
        colorScrollJMenuItem = new JCheckBoxMenuItem("색상스크롤"); 
        gradationJMenuItem = new JCheckBoxMenuItem("그라데이션"); 
        windowJMenu.add(toolJMenuItem); 
        windowJMenu.add(infoJMenuItem); 
        windowJMenu.add(colorButtonJMenuItem); 
        windowJMenu.add(colorScrollJMenuItem); 
        windowJMenu.add(gradationJMenuItem); 

        jmb.add(fileJMenu); 
        jmb.add(windowJMenu); 

        return jmb; 
    } 


    public static void main(String args[]){ 
        new Grim2(); 
    } 
} 


