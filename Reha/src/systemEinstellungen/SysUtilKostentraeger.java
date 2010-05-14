package systemEinstellungen;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

import sqlTools.SqlInfo;
import systemTools.JCompTools;



import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SysUtilKostentraeger extends JXPanel implements KeyListener, ActionListener {
	
	JXTable ktrtbl = null;
	MyKtraegerModel ktrmod = null;
	JButton[] but = {null,null,null,null};
	
	public SysUtilKostentraeger(){
		super(new BorderLayout());
		System.out.println("Aufruf SysUtilKostentraeger");
		this.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 20));
		/****/
		setBackgroundPainter(Reha.thisClass.compoundPainter.get("SystemInit"));
		/****/
	     add(getVorlagenSeite());
	     /*
		*/
		return;
	}
	/************** Beginn der Methode f�r die Objekterstellung und -platzierung *********/
	private JPanel getVorlagenSeite(){
        //                                      1.            2.    3.    4.     5.     6.    7.      8.     9.
		FormLayout lay = new FormLayout("right:max(60dlu;p), 4dlu, 40dlu, 4dlu, 40dlu, 4dlu, 40dlu, 4dlu, 40dlu",
       //1.    2. 3.   4.   5.    6.     7.  8.    9.  10.  11. 12. 13.  14.  15. 16.  17. 18.  19.   20.    21.   22.   23.
		"p, 10dlu,p, 10dlu,100dlu,10dlu, p, 10dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p,  10dlu ,10dlu, 10dlu, p");
		
		PanelBuilder builder = new PanelBuilder(lay);
		builder.setDefaultDialogBorder();
		builder.getPanel().setOpaque(false);
		CellConstraints cc = new CellConstraints();
		
		builder.addSeparator("Kostenträgerdateien abholen (IKK-Bundesverband)",cc.xyw(1,1,9));
		but[0] = new JButton("Serverkontakt herstellen");
		but[0].setActionCommand("serverkontakt");
		but[0].addActionListener(this);
		builder.add(but[0],cc.xy(1,3));
		
		ktrmod = new MyKtraegerModel();
		ktrmod.setColumnIdentifiers(new String[] {"Kostenträger","gültig ab","Dateiname"});
		ktrtbl = new JXTable(ktrmod);
		ktrtbl.getColumn(1).setMaxWidth(75);
		ktrtbl.getColumn(2).setMinWidth(0);
		ktrtbl.getColumn(2).setMaxWidth(0);
		ktrtbl.setSortable(false);
		JScrollPane jscr = JCompTools.getTransparentScrollPane(ktrtbl);
		jscr.validate();
		builder.add(jscr,cc.xywh(1,5,9,1));
		
		builder.addSeparator("Gewählte Datei in eigene Kostenträger einlesen",cc.xyw(1,7,9));
		
		but[1] = new JButton("abholen und verarbeiten");
		but[1].setActionCommand("abholen");
		but[1].addActionListener(this);
		builder.add(but[1],cc.xy(1,9));
		return builder.getPanel();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if(cmd.equals("serverkontakt")){
		     try{
				starteSession("","");
		     } catch (IOException e1) {
				e1.printStackTrace();
		     }
		     return;
		}
		if(cmd.equals("abholen")){
			int row;
			if( (row = ktrtbl.getSelectedRow()) >= 0){
				try {
					holeKtraeger((String)ktrmod.getValueAt(row, 2));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			return;
		}
		
	}
	private void starteSession(String land,String jahr) throws IOException{
		String urltext = "http://www.gkv-datenaustausch.de/Leistungserbringer_Sole_Kostentraegerdateien.gkvnet";
		String text = null;
		URL url = new URL(urltext);
		   
		      URLConnection conn = url.openConnection();
		      //System.out.println(conn.getContentEncoding());
		      

		      BufferedReader inS = new BufferedReader( new InputStreamReader( conn.getInputStream() ));
		      int durchlauf = 0;
		      //Vector<Vector<String>> ktraegerdat = new Vector<Vector<String>>();
		      Vector<String> kassendat = new Vector<String>();
		      int index;
		      boolean gestartet = false;
		      while ( (text  = inS.readLine())!= null ) {
		    	  text = makeUTF8(text);
		    	  if(durchlauf > 0){
		        	  if(text.indexOf("<h2>Kostentr") >= 0){
		        		  kassendat.clear();
		        		  gestartet = true;
		        		  text = text.replace("<h2>", "");
		        		  text = text.replace("</h2>", "");
		        		  kassendat.add(text.trim());
		        		  continue;
		        	  }
		        	  if( (index = text.indexOf("\">gültig")) >= 0){
		        		  text = text.substring(index+2);
		        		  text = text.replace("</span>", "");
		        		  text = text.replace("gültig ab", "");
		        		  text = text.replace("dem", "");
		        		  kassendat.add(text.trim());
		        		  continue;
		        	  }
		        	  if( ((index = text.indexOf("href=\"/upload/")) >= 0) && (gestartet)  ){
		        		  text = text.substring(index+6);
		        		  text = text.substring(0,text.indexOf("\""));
		        		  text = text.replace("/upload/", "");
		        		  kassendat.add(text.trim());
		        		  ktrmod.addRow((Vector<String>)kassendat.clone());
		        		  //ktraegerdat.add((Vector<String>)kassendat.clone());
		        		  gestartet = false;
		        		  continue;
		        	  }
		        	  

		          }
		          ++durchlauf;
		      }
		inS.close();
		if(ktrmod.getRowCount() > 0){
			ktrtbl.setRowSelectionInterval(0, 0);
		}
	}
	public static String makeUTF8(final String toConvert){
		try {
			return new String(toConvert.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

/*************************/
	private void holeKtraeger(String datei) throws IOException{
		String urltext = "http://www.gkv-datenaustausch.de/upload/"+datei;
		String text = null;
		URL url = new URL(urltext);
		   
		      URLConnection conn = url.openConnection();
		      //System.out.println(conn.getContentEncoding());
		      
		      int lang=0,gesamt;
		      BufferedReader inS = new BufferedReader( new InputStreamReader( conn.getInputStream() ));
		      gesamt = conn.getContentLength();
		      JOptionPane.showMessageDialog(null, "Länge des Contents = "+gesamt);
		      System.out.println("Länge des Contents = "+conn.getContentLength());
		      boolean start = false;
		      Vector<Vector<String>> ktraegerdat = new Vector<Vector<String>>();
		      Vector<String> kassendat = new Vector<String>();
		      int index;
		      boolean gestartet = false;
		      while ( (text  = inS.readLine())!= null ) {
		    	  if(text.startsWith("IDK+")){
		    		  gestartet = true;
		    		  kassendat.clear();
		    		  kassendat.add(text);
		    		  continue;
		    	  }
		    	  if((!text.startsWith("UNT+")) && gestartet){
		    		  kassendat.add(text);
		    		  continue;
		    	  }
		    	  if(text.startsWith("UNT+") && gestartet){
		    		  kassendat.add(text);
		    		  ktraegerdat.add( (Vector<String>)kassendat.clone());
		    		  gestartet = false;
		    		  continue;
		    	  }
		    	  lang+=text.length();
		      }
		inS.close();
		kassendat.clear();
		kassendat = null;
		for(int i = 0; i < ktraegerdat.size(); i++){
			ktrAuswerten(ktraegerdat.get(i));
		}
		/***2-te Stufe***/
		// hole feld ikdaten wo ikdaten nicht leer und email=leer
		Vector<Vector<String>> vec1 = SqlInfo.holeFelder("select ikkostentraeger from ktraeger where ikdaten ='' AND email='' ORDER BY id");
		System.out.println("Anzahl der felder ohne Emaildaten = "+vec1.size());
		System.out.println("***********************************************");
		Vector<String> vec2 = new Vector<String>();
		Vector<Vector<String>> mailvec;
		Vector<Vector<String>> dumyvec;
		Vector<Vector<String>> dummyvec;
		String aemail="";
		String papier="";
		String entschluessel="";
		String kostentr = "";
		String sdummy = "";
		String daten = "";
		for(int i = 0;i < vec1.size();i++){
			kostentr = vec1.get(i).get(0);
			if( (!vec2.contains(kostentr)) && (!kostentr.equals("")) ){
				
				vec2.add(kostentr);
				try{
					System.out.println("IK-Kostenträger addiert = "+kostentr);
					//Vom Kostenträger die IK-Datenholen
					dummyvec = SqlInfo.holeFelder("select ikdaten,ikpapier,ikentschluesselung from ktraeger where ikkasse='"+kostentr+"' LIMIT 1");
					daten = dummyvec.get(0).get(0);
					papier = dummyvec.get(0).get(1);
					entschluessel = dummyvec.get(0).get(2);
					//Von der Datenannahmestelle die Email holen
					dumyvec = SqlInfo.holeFelder("select email from ktraeger where ikkasse='"+daten+"' LIMIT 1");
					aemail = dumyvec.get(0).get(0);
					
					SqlInfo.sqlAusfuehren("update ktraeger set ikdaten='"+daten+"', "+
							"ikpapier='"+papier+"', "+
							"ikentschluesselung='"+entschluessel+"', "+
							"email='"+aemail+"' where ikkostentraeger='"+kostentr+"'");
				}catch(Exception ex){
					//ex.printStackTrace();
				}
				
				
			}
		}
		
		
			
	}
	private void ktrAuswerten(Vector<String> ktr){
		String ikkost ="";
		String ikdat = "";
		String ikpap = "";
		String ikent = "";
		String xemail = "";
		String nam1="",nam2="",nam3="";
		String adr1="",adr2="",adr3="";
		
		String dummy1 = "";
		String ikkas = "";
		
		String[] spdummy;
		String cmd = "";
		
		int lang;
		
		spdummy = ktr.get(0).split("\\+");
		ikkas = spdummy[1];

		
		for(int i = 1;i < ktr.size();i++){
			if(ktr.get(i).indexOf("VKG+01+") >= 0){
				// Verweis auf den Kostenträger
				ikkost = ktr.get(i).split("\\+")[2]; 
			}
			if(ktr.get(i).indexOf("VKG+03+") >= 0){
				//Verweis auf Datenannahme mit Entschlüsselung //Schlüssel 07 Art der Datenlieferung
				spdummy = ktr.get(i).split("\\+");
				if(spdummy[5].equals("07")){
					ikdat = spdummy[2];
					ikent = spdummy[2];
				}	
			}
			if(ktr.get(i).indexOf("VKG+02+") >= 0){
				//Verweis auf Datenannahme ohne Entschlüsselung //Schlüssel 07 Art der Datenlieferung
			}
			if(ktr.get(i).indexOf("VKG+09+") >= 0){
				//Verweis auf Papierannahmestelle
				spdummy = ktr.get(i).replace("'","").split("\\+");
				if(   (spdummy[5].equals("28") || spdummy[5].equals("29")) &&
						(spdummy[9].equals("00") || spdummy[9].equals("20"))	){
					ikpap = spdummy[2];
				}	
			}
			if(ktr.get(i).indexOf("NAM+01+") >= 0){
				//Name der Kasse
				spdummy = ktr.get(i).replace("'","").split("\\+");
				lang = spdummy.length-2;
				for(int i2 = 0;i2 < lang;i2++){
					if(i2==0){nam1 = spdummy[i2+2];}
					if(i2==1){nam2 = spdummy[i2+2];}
					if(i2==2){nam3 = spdummy[i2+2];}
				}
			}
			if(ktr.get(i).indexOf("ANS+1+") >= 0){
				//Anschrift der Kasse
				spdummy = ktr.get(i).replace("'","").split("\\+");
				lang = spdummy.length-2;
				for(int i2 = 0;i2 < lang;i2++){
					if(i2==0){adr1 = spdummy[i2+2];}
					if(i2==1){adr2 = spdummy[i2+2];}
					if(i2==2){adr3 = spdummy[i2+2];}
				}
			}
			//DFU+01+070+++++DTA@KV-Service-Plus-GmbH.de'
			if(ktr.get(i).indexOf("DFU+") >= 0){
				spdummy = ktr.get(i).replace("'","").split("\\+");
				if(spdummy[2].equals("070")){
					xemail = spdummy[7];					
				}
			}

		}
		
		boolean existiert=false;
		existiert = SqlInfo.gibtsSchon("select id from ktraeger where ikkasse='"+ikkas+"' LIMIT 1");
		if(existiert){
			cmd = "update ktraeger set ";
		}else{
			cmd = "insert into ktraeger set ";
		}
		cmd = cmd+"ikkasse='"+ikkas+"', ikkostentraeger='"+ikkost+"', ikpapier='"+ikpap+"', "+
		"ikdaten='"+ikdat+"', "+
		"ikentschluesselung='"+ikent+"', name1='"+nam1+"', name2='"+nam2+"', name3='"+nam3+"', "+
		"adresse1='"+adr1+"', adresse2='"+adr2+"', adresse3='"+adr3+"', email ='"+xemail+"'"+
		(existiert ? " where ikkasse='"+ikkas+"' LIMIT 1" : "");
		System.out.println(cmd);
		SqlInfo.sqlAusfuehren(cmd);
		
	}
	
/*************************/	
	class MyKtraegerModel extends DefaultTableModel{
		private static final long serialVersionUID = 1L;

		public Class getColumnClass(int columnIndex) {
			   if(columnIndex==0){return String.class;}
			   else{return String.class;}
	}

		    public boolean isCellEditable(int row, int col) {
		        //Note that the data/cell address is constant,
		        //no matter where the cell appears onscreen.
		    	return false;
		      }
			public Object getValueAt(int rowIndex, int columnIndex) {
				String theData = (String) ((Vector)getDataVector().get(rowIndex)).get(columnIndex); 
				Object result = null;
				result = theData;
				return result;
			}
	}

}
