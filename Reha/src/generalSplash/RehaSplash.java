package generalSplash;

import hauptFenster.Reha;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.jdesktop.swingx.JXDialog;
import org.jdesktop.swingx.JXPanel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class RehaSplash extends JXDialog{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2264881674273471822L;
	public JLabel lbl = null;
	public RehaSplash thisClass;
	public RehaSplash(Image img,String string){
		super((JComponent)Reha.thisFrame.getGlassPane());
		thisClass = this;
		this.setUndecorated(true);
		this.setSize(550,75);
		this.getContentPane().add(getSplash(img,string));
		this.setLocationRelativeTo(null);
		this.setModal(false);
		toFront();
		this.setVisible(true);

		
	}
	private JXPanel getSplash(Image img,String string){
		JXPanel jpan = new JXPanel();
		jpan.setBorder(null);
		jpan.setBackground(Color.WHITE);
		FormLayout lay = new FormLayout("fill:0:grow(0.50),p,fill:0:grow(0.50)","fill:0:grow(0.50),p,fill:0:grow(0.50)");
		CellConstraints cc = new CellConstraints();
		jpan.setLayout(lay);
		lbl = new JLabel(string);
		lbl.setForeground(Color.RED);
		lbl.setFont(new Font("Tahoma",Font.BOLD,12));
		jpan.add(lbl,cc.xy(2,2));
		return jpan;
	}
	
	public void setNewText(String txt){
		lbl.setText(txt);
		lbl.repaint();
		try {
			Thread.sleep(30);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void splashBeenden(boolean beenden){
		if(beenden){
			setVisible(false);
			dispose();
		}
	}

}
