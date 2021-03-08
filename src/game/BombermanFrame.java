package game;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import java.awt.event.KeyEvent;

public class BombermanFrame extends JFrame
{
    private Floor floor; 
    private BombermanComponent bombermanComponent;

    public BombermanFrame(final String title, Floor floor) throws HeadlessException {
	super(title);
	this.floor = floor;
	this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	bombermanComponent = new BombermanComponent(floor);
	floor.createPlayer(bombermanComponent, floor);
	setKeyStrokes();

	this.setLayout(new BorderLayout());
	this.add(bombermanComponent, BorderLayout.CENTER);
	this.pack();
	this.setVisible(true);
    }

    public BombermanComponent getBombermanComponent() {
	return bombermanComponent;
    }

    private boolean askUser(String question) {
	return JOptionPane.showConfirmDialog(null, question, "", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    private void setKeyStrokes() {

	KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
	bombermanComponent.getInputMap().put(stroke, "q");
	bombermanComponent.getActionMap().put("q", quit);
    }

    private final Action quit = new AbstractAction()
    {
	public void actionPerformed(ActionEvent e) {
		dispose();
	    
	}
    };
}

