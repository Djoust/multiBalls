import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import java.util.Random;

public class Ballen extends JFrame {
  public static void main(String[] args) {
    JFrame frame = new JFrame( "Stuiter Bal" );
    frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    Wereld wereld = new Wereld();
    JLabel label = new JLabel("Click and drag to shoot ball");
    label.setBounds(20,20,200,30);
    frame.add(label);
    frame.add(wereld);
    frame.setSize( 1000, 700 );
    frame.setVisible( true );
  } //end main
} // end class Ballen

class Wereld extends JComponent implements MouseMotionListener, MouseListener, ActionListener {
  private double delay = Global.DELAY; // delay for timer (milli seconds)
  private int x,y;
  private int xPressed, yPressed, xReleased, yReleased;
  private int n = 10; // max number of balls
  private int m = 0; // counter num balls
  private int xR = 800, yR = 50, hR = 80, wR = 30; // dimentions Rect
  private int points = 0; // count points

  protected Bal[] balls = new Bal[n];
  protected Timer timer;

  public Wereld() {
    timer = new Timer((int)(delay), this);
    timer.start();
    this.addMouseListener(this);
    this.addMouseMotionListener(this);
  } // end constucter

  public void actionPerformed( ActionEvent e ) {
    for (int i = 0; i < n; i++) {
      if( ( balls[i] != null ) && ( balls[i].getGo() ) ) { // calculate new pos balls after shoot
        balls[i].setWidth(getWidth());
        balls[i].setHeight(getHeight());
        balls[i].move();
        // hit detection
        if ( ( ( balls[i].getXInt() > xR ) && ( balls[i].getXInt() < (xR+wR) ) &&
        ( balls[i].getYInt() > yR ) && ( balls[i].getYInt() < (yR+hR) ) ) ) {
          points++; // keep score
          balls[i] = null; // remove ball
        } // end if hit detection
      } else if( ( balls[i] != null ) && ( balls[i].getGo() == false ) ) { // drag ball
        balls[i].setWidth(getWidth());
        balls[i].setHeight(getHeight());
        balls[i].move(x,y);
      }
    } // end for
    repaint();
  } // end actionPerformed

  public void paintComponent( Graphics g ) {
    super.paintComponent( g );
    g.setColor(Color.black);
    g.drawString("Points: "+points,10 , getHeight() - 10); // paint points
    g.fillRect(xR, yR, wR, hR); // paint hit Rect
    for (int i = 0; i < n; i++) { // paint all balls
      if(balls[i] != null) {
        g.fillOval(
          balls[i].getXInt() - balls[i].getRadiusInt(),
          balls[i].getYInt() - balls[i].getRadiusInt(),
          balls[i].getRadiusInt()*2,
          balls[i].getRadiusInt()*2
        );
      }
    } // end for
  } // end paintComponent

  @Override
  public void mousePressed(MouseEvent e) { // first
    if ( (e.getButton() == 1) && (m < n) ) {
      x = e.getX();
      y = e.getY();
      xPressed = x;
      yPressed = y;
      balls[m] = new Bal(x,y,0,0);
    } // end if button == 1
  } // end mousePressed
  @Override
  public void mouseReleased(MouseEvent e) { // second
    if ( (e.getButton() == 1) && (m < n) ) {
      x = e.getX();
      y = e.getY();
      xReleased = x;
      yReleased = y;
      balls[m].setVelX((double)(xPressed-xReleased)*2);
      balls[m].setVelY((double)(yPressed-yReleased)*2);
      balls[m].setGo(true);
      m++;
      m%=n;
    } // end if button == 1
  } // end mouseReleased
  @Override
  public void mouseClicked(MouseEvent e) { } // third
  @Override
  public void mouseEntered(MouseEvent e) { }
  @Override
  public void mouseExited(MouseEvent e) { }

  @Override
  public void mouseDragged(MouseEvent e) {
    if ( (e.getButton() == 1) && (m < n) ) {
      x = e.getX();
      y = e.getY();
    }
  } // end mouseDragged
  @Override
  public void mouseMoved(MouseEvent e) { }

} // end class Wereld

class Bal {
  private double x, y, radius, velX, accX, velY, accY, mass, bounce;
  private double fy = 0, fx = 0; // force x/y;
  private double dx, dy, new_ax, avg_ax, new_ay, avg_ay; // delta, average, new(feedback)
  private int width, height;
  private boolean go = false;
  protected Random rand = new Random();

  public Bal(int x, int y, int velX, int velY) {
    this.x = (double)(x);
    this.y = (double)(y);
    this.velX = (double)(velX); //velocity x
    this.velY = (double)(velY); // velocity y
    accX  = 0; // acceleration x
    accY = 0; // acceleration y
    radius = 15; // ball radius in cm or px
    mass = 10; // mass ball in kg
    bounce = -0.65;
  } // end constructor bal

  // calculate new possition
  public void move(){
    fy += mass * Global.GRAVITY;
    // Verlet integration x
    dx = velX * Global.DT - 0.5 * accX * Global.DT * Global.DT; // delta x
    x += dx;
    new_ax = fx / mass; // new acceleration x
    avg_ax = 0.5 * (new_ax + accX); // average acceleration x
    velX += avg_ax * Global.DT;

    // Verlet integration y
    dy = velY * Global.DT - 0.5 * accY * Global.DT * Global.DT; // delta y
    y += dy;
    new_ay = fy / mass; // new acceleration y
    avg_ay = 0.5 * (new_ay + accY); // average acceleration y
    velY += avg_ay * Global.DT;

    // collision detection
    if ( ( x - radius < (double)(0) ) && ( velX < 0 ) ) { velX *= bounce; }; // left
    if ( ( x + radius > (double)(width) ) && ( velX > 0 ) ) { velX *= bounce; }; // rigth
    if ( ( y - radius < (double)(0) ) && ( velY < -0.1 ) ) { velY *= bounce; }; // top
    if ( ( y + radius > (double)(height) ) && ( velY > -0.1 ) ) { velY *= bounce; }; // bottom
  }

  public void move(int x, int y){
    this.x = (double)(x);
    this.y = (double)(y);
  }

  // getters
  public double getX() { return x; }
  public double getY() { return y; }
  public double getRadius() { return radius; }
  public int getXInt() { return (int)(x); }
  public int getYInt() { return (int)(y); }
  public int getRadiusInt() { return (int)(radius); }
  public double getVelX() { return velX; }
  public double getAccX() { return accX; }
  public double getVelY() { return velY; }
  public double getAccY() { return accY; }
  public double getMass() { return mass; }
  public double getbounce() { return bounce; }
  public int getWidth() { return width; }
  public int getHeight() { return height; }
  public boolean getGo() { return go; }
  // setters
  public void setX(double x) { this.x = x; }
  public void setY(double y) { this.y = y; }
  public void setRadius(double radius) { this.radius = radius; }
  public void setVelX(double velX) { this.velX = velX; }
  public void setAccX(double accX) { this.accX = accX; }
  public void setVelY(double velY) { this.velY = velY; }
  public void setAccY(double accY) { this.accY = accY; }
  public void setMass(double mass) { this.mass = mass; }
  public void setBounce(double bounce) { this.bounce = bounce; }
  public void setWidth(int width) { this.width = width; }
  public void setHeight(int height) { this.height = height; }
  public void setGo(boolean go) {this.go = go; }
} // end class bal

class Global {
  public static final double GRAVITY = 9.81;
  public static final int FPS = 60; // frames per second
  public static final double DT = 1.0/(double)(FPS); // delta time (seconds)
  public static final double DELAY = DT*1000.0; // delay for timer (milli seconds)
}


/*
TO DO:
Improve bounce (y > 0)
Rebuilt structure
  - class Object
    - Thread
  - class Ball uit Object
    - propperties: X, Y, radius, mass, accX, velX, accY, velY, bounce
  - class wereld (berekent nieuw pos)
Collision detection met elkaar
*/
