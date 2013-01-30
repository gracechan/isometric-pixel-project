import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

@SuppressWarnings("serial")
public class FileMenu extends JMenu implements ActionListener {
	private Canvas canvas;
	private JMenuItem new_, save, open;

	public FileMenu(Canvas canvas) {
		super("File");
		this.canvas = canvas;
		new_ = new JMenuItem("New");
		new_.setAccelerator(KeyStroke.getKeyStroke('N', KeyEvent.CTRL_DOWN_MASK));
		add(new_);
		new_.addActionListener(this);
		save = new JMenuItem("Save");
		save.setAccelerator(KeyStroke.getKeyStroke('S', KeyEvent.CTRL_DOWN_MASK));
		add(save);
		save.addActionListener(this);
		open = new JMenuItem("Open");
		open.setAccelerator(KeyStroke.getKeyStroke('O', KeyEvent.CTRL_DOWN_MASK));
		add(open);
		open.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == new_) {
			System.out.println("New file");
		}

		else if(e.getSource() == save) {
			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory(new java.io.File("."));
			chooser.setDialogTitle("Save");
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.setAcceptAllFileFilterUsed(false);

			if (chooser.showSaveDialog(canvas.getParent()) == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				System.out.println("Save file");
			} else {
				System.out.println("No Selection ");
			}
		}		

		else if(e.getSource() == open) {
			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory(new java.io.File("."));
			chooser.setDialogTitle("Save");
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.setAcceptAllFileFilterUsed(false);

			if (chooser.showOpenDialog(canvas.getParent()) == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				System.out.println("Open file");
			} else {
				System.out.println("No Selection ");
			}
		}
	}
}