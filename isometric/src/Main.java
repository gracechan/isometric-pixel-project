//import geometry.Point3D;
//import geometry.Shape3D;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

@SuppressWarnings("serial")
public class Main extends JFrame {
	private CanvasData canvasData;
	private Canvas canvas;
	private static final int width = 600;
	private static final int height = 600;

	public Main() {
		super("Isometric Tool");
		setSize(width, height);

		// canvas
		canvasData = new CanvasData();

		// add some test shapes
		canvasData.addShapes(TestShapes.robot());
		canvas = new Canvas(canvasData);
		add(canvas);

		// menu 
		JMenuBar mb = new JMenuBar(); 
		FileMenu fileMenu = new FileMenu(canvas);
		ActionsMenu actionsMenu = new ActionsMenu(canvas);
		mb.add(fileMenu); 
		mb.add(actionsMenu);
		setJMenuBar(mb);

		// key listeners
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new MyDispatcher());

		// exit program
		addWindowListener(new WindowAdapter() { 
			public void windowClosing(WindowEvent e) { 
				exit(); 
			} 
		});
	}

	public static void main(String[] args) {
		Main app = new Main();
		app.setVisible(true);
	}

	private class MyDispatcher implements KeyEventDispatcher {
        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
        	switch(e.getID()) {
        		case KeyEvent.KEY_PRESSED: canvas.keyPressed(e); break;
        		case KeyEvent.KEY_RELEASED: canvas.keyReleased(e); break;
        		case KeyEvent.KEY_TYPED: canvas.keyTyped(e); break;
        	}
            return false;
        }
    }

	public void exit() { 
		setVisible(false); // hide the Frame 
		dispose(); // tell windowing system to free resources 
		System.exit(0); // exit 
	}
} 
