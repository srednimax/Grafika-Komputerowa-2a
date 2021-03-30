import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;

import static com.jogamp.opengl.GL.GL_TRIANGLE_FAN;
import static com.jogamp.opengl.GL2.GL_POLYGON;
import static com.jogamp.opengl.fixedfunc.GLLightingFunc.*;

/**
 * CPSC 424, Fall 2015, Lab 6:  Light and Material in OpenGL 1.1.
 * This program shows a square "stage" with a variety of objects
 * arranged on it.  The objects use several shapes and materials
 * and include a wireframe object that is drawn with lighting
 * turned off. The user can rotate the stage about the y-axis
 * by dragging the mouse horizontally.
 */
public class Lab6 extends GLJPanel implements GLEventListener {

    private double rotateY = 0;  // rotation of view about the y-axis, controlled by mouse.

    /**
     * A main routine to create and show a window that contains a
     * panel of type Lab4.  The program ends when the user closes the
     * window.
     */
    public static void main(String[] args) {
        JFrame window = new JFrame("Stage");
        Lab6 panel = new Lab6();
        window.setContentPane(panel);
        window.pack();
        window.setResizable(false);
        window.setLocation(50, 50);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
    }

    /**
     * Constructor for class Lab6, sets the preferred size and configures event listeners
     */
    public Lab6() {
        super(new GLCapabilities(null)); // Makes a panel with default OpenGL "capabilities".
        setPreferredSize(new Dimension(1000, 500));
        addGLEventListener(this); // This panel will respond to OpenGL events.
        MouseHandler mouser = new MouseHandler();
        addMouseListener(mouser);  // The MouseHandler will respond to mouse events.
    }

    // --------------------------- Data for some materials ---------------------------------------------------

    /**
     * One of the rows of this array corresponds to a set of material properties.  Items 0 to 4 in a row
     * specify an ambient color; items 4 through 7, a diffuse color; items 8 through 11, a specular color;
     * and item 12, a specular exponent (shininess value).  The data is adapted from the table on the page
     * http://devernay.free.fr/cours/opengl/materials.html
     */
    private final static float[][] materials = {
            { /*0 "emerald" */   0.0215f, 0.1745f, 0.0215f, 1.0f, 0.07568f, 0.61424f, 0.07568f, 1.0f, 0.633f, 0.727811f, 0.633f, 1.0f, 0.6f * 128},
            { /*1 "jade" */   0.135f, 0.2225f, 0.1575f, 1.0f, 0.54f, 0.89f, 0.63f, 1.0f, 0.316228f, 0.316228f, 0.316228f, 1.0f, 0.1f * 128},
            { /*2 "obsidian" */   0.05375f, 0.05f, 0.06625f, 1.0f, 0.18275f, 0.17f, 0.22525f, 1.0f, 0.332741f, 0.328634f, 0.346435f, 1.0f, 0.3f * 128},
            { /*3 "pearl" */   0.25f, 0.20725f, 0.20725f, 1.0f, 1.0f, 0.829f, 0.829f, 1.0f, 0.296648f, 0.296648f, 0.296648f, 1.0f, 0.088f * 128},
            { /*4 "ruby" */   0.1745f, 0.01175f, 0.01175f, 1.0f, 0.61424f, 0.04136f, 0.04136f, 1.0f, 0.727811f, 0.626959f, 0.626959f, 1.0f, 0.6f * 128},
            { /*5 "turquoise" */   0.1f, 0.18725f, 0.1745f, 1.0f, 0.396f, 0.74151f, 0.69102f, 1.0f, 0.297254f, 0.30829f, 0.306678f, 1.0f, 0.1f * 128},
            { /*6 "brass" */   0.329412f, 0.223529f, 0.027451f, 1.0f, 0.780392f, 0.568627f, 0.113725f, 1.0f, 0.992157f, 0.941176f, 0.807843f, 1.0f, 0.21794872f * 128},
            { /*7 "bronze" */   0.2125f, 0.1275f, 0.054f, 1.0f, 0.714f, 0.4284f, 0.18144f, 1.0f, 0.393548f, 0.271906f, 0.166721f, 1.0f, 0.2f * 128},
            { /*8 "chrome" */   0.25f, 0.25f, 0.25f, 1.0f, 0.4f, 0.4f, 0.4f, 1.0f, 0.774597f, 0.774597f, 0.774597f, 1.0f, 0.6f * 128},
            { /*9 "copper" */   0.19125f, 0.0735f, 0.0225f, 1.0f, 0.7038f, 0.27048f, 0.0828f, 1.0f, 0.256777f, 0.137622f, 0.086014f, 1.0f, 0.1f * 128},
            { /*10 "gold" */   0.24725f, 0.1995f, 0.0745f, 1.0f, 0.75164f, 0.60648f, 0.22648f, 1.0f, 0.628281f, 0.555802f, 0.366065f, 1.0f, 0.4f * 128},
            { /*11 "silver" */   0.19225f, 0.19225f, 0.19225f, 1.0f, 0.50754f, 0.50754f, 0.50754f, 1.0f, 0.508273f, 0.508273f, 0.508273f, 1.0f, 0.4f * 128},
            { /*12 "cyan plastic" */   0.0f, 0.1f, 0.06f, 1.0f, 0.0f, 0.50980392f, 0.50980392f, 1.0f, 0.50196078f, 0.50196078f, 0.50196078f, 1.0f, .25f * 128},
            { /*13 "green plastic" */   0.0f, 0.0f, 0.0f, 1.0f, 0.1f, 0.35f, 0.1f, 1.0f, 0.45f, 0.55f, 0.45f, 1.0f, .25f * 128},
            { /*14 "red plastic" */   0.0f, 0.0f, 0.0f, 1.0f, 0.5f, 0.0f, 0.0f, 1.0f, 0.7f, 0.6f, 0.6f, 1.0f, .25f * 128},
            { /*15 "cyan rubber" */   0.0f, 0.05f, 0.05f, 1.0f, 0.4f, 0.5f, 0.5f, 1.0f, 0.04f, 0.7f, 0.7f, 1.0f, .078125f * 128},
            { /*16 "green rubber" */   0.0f, 0.05f, 0.0f, 1.0f, 0.4f, 0.5f, 0.4f, 1.0f, 0.04f, 0.7f, 0.04f, 1.0f, .078125f * 128},
            { /*17 "red rubber" */   0.05f, 0.0f, 0.0f, 1.0f, 0.5f, 0.4f, 0.4f, 1.0f, 0.7f, 0.04f, 0.04f, 1.0f, .078125f * 128},
    };


    //-------------------------- OpenGL methods from GLEventListener ---------------

    private GLUT glut = new GLUT(); // An object for drawing GLUT shapes.
    private GLU glu = new GLU(); // An object for calling GLU functions.

    private void piramid(GL2 gl2) {
        float x;
        float y;
        float size = 3.5f;
        //wielokąt
        gl2.glColor3f(0.6f, 0.01f, 0.46f);
        gl2.glBegin(GL_POLYGON);
        for (int angle = 0; angle < 360; angle += 40) {

            x = (float) (size * Math.cos(Math.toRadians(angle)));
            y = (float) (size * Math.sin(Math.toRadians(angle)));
            gl2.glVertex3f(x, 0, y);
        }
        gl2.glEnd();
        //pokrywa

        float r = 1, g = 0, b = 1;
        for (int i = 0; i < 9; i++) {

            //gl2.glColor3f(r,g,b);
            gl2.glPushMatrix();
            gl2.glRotatef(10 + i * 40, 0f, 1f, 0f);
            gl2.glTranslatef(-0.0f * size, 0f, -0.95f * size);
            gl2.glScalef(0.88f, 6f, 1f);
            gl2.glRotatef(75, 1f, 0f, 0f);
            //swiatlo
           // gl2.glNormal3f(0f,1f,0f);
           gl2.glNormal3f((float)Math.cos(Math.toRadians(70)),(float)Math.sin(Math.toRadians(70)),0f);
            triangle(gl2, size);
            gl2.glPopMatrix();
            b -= 0.1f;
        }
        gl2.glPopMatrix();

    }

    //funkcja do rysowania trójkąta
    private void triangle(GL2 gl2, float size) {

        gl2.glBegin(GL_TRIANGLE_FAN);
        gl2.glVertex3f(-0.4f * size, 0, 0f);
        gl2.glVertex3f(0.4f * size, 0, 0f);
        gl2.glVertex3f(0f, size, 0f);
        gl2.glEnd();
    }

    /**
     * The display method is called when the panel needs to be drawn.
     * Here, it draws a stage and some objects on the stage.
     */
    public void display(GLAutoDrawable drawable) {

        GL2 gl2 = drawable.getGL().getGL2(); // The object that contains all the OpenGL methods.

        gl2.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        gl2.glLoadIdentity();
        glu.gluLookAt(0, 8, 40, 0, 1, 0, 0, 1, 0);  // viewing transform

        gl2.glRotated(rotateY, 0, 1, 0);  // modeling transform: rotation of the scene about y-axis

        float[] gray = {0.6f, 0.6f, 0.6f, 1};
        float[] zero = {0, 0, 0, 1};
        //gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, gray, 0);
        //gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, zero, 0);

        gl2.glPushMatrix();
        gl2.glTranslated(0, -1.5, 0); // Move top of stage down to y = 0
        gl2.glScaled(1, 0.05, 1); // Stage will be one unit thick,
        //gl2.glColor3f(1f,1f,1f);
        glut.glutSolidCube(20);
        gl2.glPopMatrix();

        // TODO draw some shapes!
        piramid(gl2);

        //gl2.glClear(GL2.GL_COLOR_BUFFER_BIT);

    } // end display()


    /**
     * The init method is called once, before the window is opened, to initialize
     * OpenGL.  Here, it sets up a projection, configures some lighting, and enables
     * the depth test.
     */
    public void init(GLAutoDrawable drawable) {
        GL2 gl2 = drawable.getGL().getGL2();
        gl2.glMatrixMode(GL2.GL_PROJECTION);
        gl2.glLoadIdentity();
        glu.gluPerspective(20, (double) getWidth() / getHeight(), 1, 100);
        gl2.glMatrixMode(GL2.GL_MODELVIEW);
        gl2.glEnable(GL2.GL_DEPTH_TEST);
        gl2.glEnable(GL2.GL_NORMALIZE);
        gl2.glEnable(GL2.GL_LIGHTING);


        // TODO configure better lighting!
        //swiatlo 0
        //gl2.glEnable(GL2.GL_COLOR_MATERIAL);
        //swiatlo 1
        gl2.glEnable(GL2.GL_LIGHT1);
        gl2.glLightfv(GL_LIGHT1, GL_DIFFUSE, materials[4], 0);
        gl2.glLightfv(GL_LIGHT1, GL_AMBIENT, materials[4], 0);
        float[] position = {10f, 5f, 10f, 0f};
        gl2.glLightfv(GL_LIGHT1, GL_POSITION, position, 0);
        //swiatlo 2
        gl2.glEnable(GL2.GL_LIGHT2);
        gl2.glLightfv(GL_LIGHT2, GL_DIFFUSE, materials[0], 0);
        gl2.glLightfv(GL_LIGHT2, GL_AMBIENT, materials[0], 0);
        position[0] = -10f;//x
        position[1] = 5f;//y
        position[2] = 10f;//z
        position[3] = 0f;//w
        gl2.glLightfv(GL_LIGHT2, GL_POSITION, position, 0);
        //swiatlo 3
        gl2.glEnable(GL2.GL_LIGHT3);
        gl2.glLightfv(GL_LIGHT3, GL_DIFFUSE, materials[8], 0);
        position[0] = 0f;//x
        position[1] = 10f;//y
        position[2] = 0f;//z
        position[3] = 0f;//w
        gl2.glLightfv(GL_LIGHT3, GL_POSITION, position, 0);
        //global
        float ambient [] = {0.95f,0.95f,0.95f,1};
        gl2.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, ambient, 0);
        gl2.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, 1);
    }


    public void dispose(GLAutoDrawable drawable) {
        // called when the panel is being disposed
    }


    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        // called when user resizes the window
    }

    //----------------- A class to support simple mouse interaction; ----------
    //----------------- horizontal mouse motion rotates about y-axis ----------

    private class MouseHandler extends MouseAdapter {

        private int prevX;  // Previous mouse x-coord during a drag gesture.
        private boolean dragging;  // Set to true during dragging.

        public void mouseDragged(MouseEvent evt) {
            if (dragging) {
                int x = evt.getX(); // current x coord of mouse
                double dx = x - prevX; // change in mouse coord
                rotateY += dx / 7;
                repaint(); // redraw the scene
                prevX = x;
            }
        }

        public void mousePressed(MouseEvent evt) {
            if (dragging)
                return;
            prevX = evt.getX();
            dragging = true;
            Lab6.this.addMouseMotionListener(this);
        }

        public void mouseReleased(MouseEvent evt) {
            dragging = false;
            Lab6.this.removeMouseMotionListener(this);
        }

    } // end nested class MouseHandler


} // end class Lab5