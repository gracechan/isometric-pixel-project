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
	private ButtonGroup actionGroup, lineOptionGroup;
	private JMenuItem translate, distort;
	private JMenuItem gcdLine, altLine;

	public ActionsMenu(Canvas canvas) {
		super("Actions");
		this.canvas = canvas;
		actionGroup = new ButtonGroup(); lineOptionGroup = new ButtonGroup();
		
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
		
		addSeparator();
		
		altLine = new JRadioButtonMenuItem("Default");
		altLine.setSelected(true);
		canvas.setOperation(CanvasActions.TRANSLATE_OBJECT);
		lineOptionGroup.add(altLine);
		add(altLine);
		altLine.addActionListener(this);
		
		gcdLine = new JRadioButtonMenuItem("GCD Line Drawing");
		lineOptionGroup.add(gcdLine);
		add(gcdLine);
		gcdLine.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == translate) {
			System.out.println("translate");
			canvas.setOperation(CanvasActions.TRANSLATE_OBJECT);
		} else if (e.getSource() == distort) {
			System.out.println("distort");
			canvas.setOperation(CanvasActions.DISTORT_VERTEX);
		} else if (e.getSource() == gcdLine) {
			canvas.setLineOption(CanvasActions.LINE_OPTION_GCD);
			canvas.repaint();
		} else if (e.getSource() == altLine) {
			canvas.setLineOption(CanvasActions.LINE_OPTION_ALTERNATE);
			canvas.repaint();
		}
	}
}