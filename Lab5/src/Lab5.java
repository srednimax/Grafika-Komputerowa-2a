import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.gl2.GLUT;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static com.jogamp.opengl.GL.*;
import static com.jogamp.opengl.GL2.GL_POLYGON;

/**
 * CPSC 424, Fall 2015, Lab 4:  Some objects in 3D.  The arrow keys
 * can be used to rotate the object.  The number keys 1 through 6
 * select the object.  The space bar toggles the use of anaglyph
 * stereo.
 */
public class Lab5 extends GLJPanel implements GLEventListener, KeyListener {

    /**
     * A main routine to create and show a window that contains a
     * panel of type Lab5.  The program ends when the user closes the
     * window.
     */
    public static void main(String[] args) {
        JFrame window = new JFrame("Some Objects in 3D");
        Lab5 panel = new Lab5();
        window.setContentPane(panel);
        window.pack();
        window.setResizable(false);
        window.setLocation(50, 50);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
    }

    /**
     * Constructor for class Lab4.
     */
    public Lab5() {
        super(new GLCapabilities(null)); // Makes a panel with default OpenGL "capabilities".
        setPreferredSize(new Dimension(700, 700));
        addGLEventListener(this); // This panel will respond to OpenGL events.
        addKeyListener(this);  // The panel will respond to key events.
    }


    //------------------- TODO: Complete this section! ---------------------

    private int objectNumber = 4;        // Which object to draw (1 ,2, 3, 4, 5, or 6)?
    //   (Controlled by number keys.)

    private boolean useAnaglyph = false; // Should anaglyph stereo be used?
    //    (Controlled by space bar.)

    private int rotateX = 0;    // Rotations of the cube about the axes.
    private int rotateY = 0;    //   (Controlled by arrow, PageUp, PageDown keys;
    private int rotateZ = 0;    //    Home key sets all rotations to 0.)

    private GLUT glut = new GLUT(); // An object for drawing GLUT shapes.


    /**
     * The method that draws the current object, with its modeling transformation.
     */
    private void corkscrew(GL2 gl2) {
        float size = 1;
        float x;
        float y;
        float z = -5;

        float t = 0;

        float r = 1;
        float g = 1;
        float b = 1;

        for (int angle = 0; angle < 3240; angle += 10) {
            gl2.glColor3f(r, g, b);
            gl2.glPointSize(size += 0.05);
            gl2.glBegin(GL_POINTS);
            x = (float) (t * Math.cos(Math.toRadians(angle)));
            y = (float) (t * Math.sin(Math.toRadians(angle)));

            gl2.glVertex3f(x, y, z);
            z += 0.03;
            t += 0.01;
            //niebieski
            if (objectNumber == 1) {
                if (r > 0.1)
                    r -= 0.005;
                else if (g > 0.1)
                    g -= 0.005;
                else if (b > 0.1)
                    b -= 0.005;
            }
            //  czerwony
            if (objectNumber == 2) {
                if (g > 0.1)
                    g -= 0.005;
                else if (b > 0.1)
                    b -= 0.005;
                else if (r > 0.1)
                    r -= 0.005;
            }
//            //zieleony
            if (objectNumber == 3) {
                if (b > 0.1)
                    b -= 0.005;
                else if (r > 0.1)
                    r -= 0.005;
                else if (g > 0.1)
                    g -= 0.005;
            }
            gl2.glEnd();
        }
    }

    private void piramid(GL2 gl2) {
        float x;
        float y;
        float size = 3.5f;
        //wielokąt
        gl2.glColor3f(0.6f,0.01f,0.46f);
        gl2.glBegin(GL_POLYGON);
        for (int angle = 0; angle < 360; angle += 40) {

            x = (float) (size * Math.cos(Math.toRadians(angle)));
            y = (float) (size * Math.sin(Math.toRadians(angle)));
            gl2.glVertex3f(x, 0, y);
        }
        gl2.glEnd();
        //pokrywa

        float r=1,g=0,b = 1;
        for (int i = 0;i<9;i++)
        {
            gl2.glColor3f(r,g,b);
            gl2.glPushMatrix();
            gl2.glRotatef(10 + i*40,0f,1f,0f);
            gl2.glTranslatef(-0.0f * size, 0f,-0.95f * size);
            gl2.glScalef(0.88f,6f,1f);
            gl2.glRotatef(75,1f,0f,0f);
            triangle(gl2,size);
            gl2.glPopMatrix();
            b-=0.1f;
        }
        gl2.glPopMatrix();

    }
//funkcja do rysowania trójkąta
    private void triangle(GL2 gl2,float size) {

        gl2.glBegin(GL_TRIANGLE_FAN);
        gl2.glVertex3f(-0.4f * size, 0,0f);
        gl2.glVertex3f(0.4f * size, 0,0f);
        gl2.glVertex3f(0f, size,0f);
        gl2.glEnd();
    }


    private void draw(GL2 gl2) {

        gl2.glRotatef(rotateZ, 0, 0, 1);   // Apply rotations to complete object.
        gl2.glRotatef(rotateY, 0, 1, 0);
        gl2.glRotatef(rotateX, 1, 0, 0);

        // TODO: Draw the currently selected object, number 1, 2, 3, 4, 5, or 6.
        // (Objects should lie in the cube with x, y, and z coordinates in the
        // range -5 to 5.)
        if (objectNumber < 4) {
            corkscrew(gl2);
        } else if (objectNumber == 4) {
            piramid(gl2);
        }


    }

    //-------------------- Draw the Scene  -------------------------

    /**
     * The display method is called when the panel needs to be drawn.
     * It's called when the window opens and it is called by the keyPressed
     * method when the user hits a key that modifies the scene.
     */
    public void display(GLAutoDrawable drawable) {

        GL2 gl2 = drawable.getGL().getGL2(); // The object that contains all the OpenGL methods.

        if (useAnaglyph) {
            gl2.glDisable(GL2.GL_COLOR_MATERIAL); // in anaglyph mode, everything is drawn in white
            gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, new float[]{1, 1, 1, 1}, 0);
        } else {
            gl2.glEnable(GL2.GL_COLOR_MATERIAL);  // in non-anaglyph mode, glColor* is respected
        }
        gl2.glNormal3f(0, 0, 1); // (Make sure normal vector is correct for object 1.)

        gl2.glClearColor(0, 0, 0, 1); // Background color (black).
        gl2.glClear(GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);


        if (useAnaglyph == false) {
            gl2.glLoadIdentity(); // Make sure we start with no transformation!
            gl2.glTranslated(0, 0, -15);  // Move object away from viewer (at (0,0,0)).
            draw(gl2);
        } else {
            gl2.glLoadIdentity(); // Make sure we start with no transformation!
            gl2.glColorMask(true, false, false, true);
            gl2.glRotatef(4, 0, 1, 0);
            gl2.glTranslated(1, 0, -15);
            draw(gl2);  // draw the current object!
            gl2.glColorMask(true, false, false, true);
            gl2.glClear(GL2.GL_DEPTH_BUFFER_BIT);
            gl2.glLoadIdentity();
            gl2.glRotatef(-4, 0, 1, 0);
            gl2.glTranslated(-1, 0, -15);
            gl2.glColorMask(false, true, true, true);
            draw(gl2);
            gl2.glColorMask(true, true, true, true);
        }

    } // end display()

    /**
     * The init method is called once, before the window is opened, to initialize
     * OpenGL.  Here, it sets up a projection, turns on some lighting, and enables
     * the depth test.
     */
    public void init(GLAutoDrawable drawable) {
        GL2 gl2 = drawable.getGL().getGL2();
        gl2.glMatrixMode(GL2.GL_PROJECTION);
        gl2.glFrustum(-3.5, 3.5, -3.5, 3.5, 5, 25);
        gl2.glMatrixMode(GL2.GL_MODELVIEW);
        gl2.glEnable(GL2.GL_LIGHTING);
        gl2.glEnable(GL2.GL_LIGHT0);
        gl2.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, new float[]{0.7f, 0.7f, 0.7f}, 0);
        gl2.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, 1);
        gl2.glEnable(GL2.GL_DEPTH_TEST);
        gl2.glLineWidth(3);  // make wide lines for the stellated dodecahedron.
    }

    public void dispose(GLAutoDrawable drawable) {
        // called when the panel is being disposed
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        // called when user resizes the window
    }

    // ----------------  Methods from the KeyListener interface --------------

    /**
     * Responds to keypressed events.  The four arrow keys control the rotations
     * about the x- and y-axes.  The PageUp and PageDown keys control the rotation
     * about the z-axis.  The Home key resets all rotations to zero.  The number
     * keys 1, 2, 3, 4, 5, and 6 select the current object number.  Pressing the space
     * bar toggles anaglyph stereo on and off.  The panel is redrawn to reflect the
     * change.
     */
    public void keyPressed(KeyEvent evt) {
        int key = evt.getKeyCode();
        boolean repaint = true;
        if (key == KeyEvent.VK_LEFT)
            rotateY -= 6;
        else if (key == KeyEvent.VK_RIGHT)
            rotateY += 6;
        else if (key == KeyEvent.VK_DOWN)
            rotateX += 6;
        else if (key == KeyEvent.VK_UP)
            rotateX -= 6;
        else if (key == KeyEvent.VK_PAGE_UP)
            rotateZ += 6;
        else if (key == KeyEvent.VK_PAGE_DOWN)
            rotateZ -= 6;
        else if (key == KeyEvent.VK_HOME)
            rotateX = rotateY = rotateZ = 0;
        else if (key == KeyEvent.VK_1)
            objectNumber = 1;
        else if (key == KeyEvent.VK_2)
            objectNumber = 2;
        else if (key == KeyEvent.VK_3)
            objectNumber = 3;
        else if (key == KeyEvent.VK_4)
            objectNumber = 4;
        else if (key == KeyEvent.VK_5)
            objectNumber = 5;
        else if (key == KeyEvent.VK_6)
            objectNumber = 6;
        else if (key == KeyEvent.VK_SPACE)
            useAnaglyph = !useAnaglyph;
        else
            repaint = false;
        if (repaint)
            repaint();
    }

    public void keyReleased(KeyEvent evt) {
    }

    public void keyTyped(KeyEvent evt) {
    }

} // end class Lab4