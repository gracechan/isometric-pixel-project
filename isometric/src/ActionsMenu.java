import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

public class ActionsMenu extends JMenu implements ActionListener {
	private Canvas canvas;
	private ButtonGroup actionGroup;
	private JMenuItem translate, distort;

	public ActionsMenu(Canvas canvas) {
		super("Actions");
		this.canvas = canvas;
		actionGroup = new ButtonGroup();
		
		translate = new JRadioButtonMenuItem("Translate Object");
		translate.setSelected(true);
		canvas.setOperation(CanvasActions.TRANSLATE_OBJECT);
		translate.setAccelerator(KeyStroke.getKeyStroke('T', KeyEvent.CTRL_DOWN_MASK));
		actionGroup.add(translate);
		add(translate);
		translate.addActionListener(this);
		
		distort = new JRadioButtonMenuItem("Distort Vertex");
		distort.setAccelerator(KeyStroke.getKeyStroke('D', KeyEvent.CTRL_DOWN_MASK));
		actionGroup.add(distort);
		add(distort);
		distort.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == translate) {
			System.out.println("translate");
			canvas.setOperation(CanvasActions.TRANSLATE_OBJECT);
		} else if (e.getSource() == distort) {
			System.out.println("distort");
			canvas.setOperation(CanvasActions.DISTORT_VERTEX);
		}		
	}
}