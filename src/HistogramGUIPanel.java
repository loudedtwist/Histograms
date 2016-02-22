import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class HistogramGUIPanel extends Panel {
    Button b;
    HistogramPicturePanel m;
    Frame f;
    public void loadPicture(){
        if(m!=null)
            remove(m);
        FileDialog fileDialog =
                new FileDialog(f,"select a picture",FileDialog.LOAD);
        fileDialog.setDirectory("C:\\");
        fileDialog.setFile("*.jpg");
        fileDialog.setVisible(true);
        m = new HistogramPicturePanel(fileDialog.getDirectory()+fileDialog.getFile());
        add(m,BorderLayout.CENTER);
        m.drawHistagram();

        revalidate();
        repaint();
        f.pack();
    }
    class OnLoad implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            loadPicture();
        }
    }

    HistogramGUIPanel(Frame frame){
        b = new Button("load");
        b.addActionListener(new OnLoad());
        setLayout(new BorderLayout(0,0));
        add(b,BorderLayout.NORTH);
        f = frame;
        f.setLocation((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2 - (50),(int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2-(50));
        f.setBackground(new Color(72, 84, 85));
    }

    static public void main(String ... args){
        Frame f = new Frame("HISTOGRAM");
        HistogramGUIPanel hgp = new HistogramGUIPanel(f);
        f.add(hgp);
        f.pack();
        f.setVisible(true);
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                System.exit(0);
            }
        });
    }

}
