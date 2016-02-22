import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.RGBImageFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;

public class HistogramPicturePanel extends Panel {
    class MyFilter extends RGBImageFilter
    {
        int w,h,p[];
        public MyFilter(int w, int h,int p[])
        {
            canFilterIndexColorModel = true;
            this.w = w;this.h = h;this.p = p;
        }
        public int filterRGB(int x, int y, int rgb)
        {int r,g,b;
            r=(rgb & 0x00ff0000)>>>16;
            g=(rgb & 0x0000ff00)>>>8;
            b=(rgb & 0x000000ff);
            p[(r+g+b)/3]+=1;
            return rgb;
        }
    }

    private Image img;
    private Image imgFiltered;
    private BufferedImage bImg;
    public int hist[];
    public int histSorted[];
    private int w,h,prozent;
    private int sizeOfCol;

    private int getMittelwertPixel(int rgb){
        int r,g,b;
            r=(rgb & 0x00ff0000)>>>16;
            g=(rgb & 0x0000ff00)>>>8;
            b=(rgb & 0x000000ff);
            return (r+g+b)/3;
    }
    public void initHistogramData(){
        int w = this.w;
        int h = this.h;
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int pixel = bImg.getRGB(j, i);
                int index = getMittelwertPixel(pixel);
                hist[index]+=1;
                histSorted[index]+=1;
            }
        }
        Arrays.sort(histSorted);
        prozent=histSorted[histSorted.length-1]/100;
        System.out.println("one % = " +  prozent);

    }
    HistogramPicturePanel(String path){
        hist = new int[256];histSorted = new int[256];
        try {bImg = ImageIO.read(new FileInputStream(path));} catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Not a picture", "InfoBox: " + "FileDialog", JOptionPane.INFORMATION_MESSAGE);System.exit(0);}
        MediaTracker M=new MediaTracker(this);
        M.addImage(bImg,1);
        try {M.waitForID(1);}catch (Exception e){}
        w = bImg.getWidth(this); h = bImg.getHeight(this);
        setBackground(new Color(72, 84, 85));
        while(true) {
            if (w > Toolkit.getDefaultToolkit().getScreenSize().getWidth() || h > Toolkit.getDefaultToolkit().getScreenSize().getHeight()) {
                w /= 2;
                h /= 2;
                AffineTransform tx = new AffineTransform();
                tx.scale(0.5, 0.5);
                AffineTransformOp op = new AffineTransformOp(tx,
                        AffineTransformOp.TYPE_BILINEAR);
                bImg = op.filter(bImg, null);
            }
            else break;
        }
        sizeOfCol=w/256;if(sizeOfCol==0)sizeOfCol++;
        imgFiltered = createImage(new FilteredImageSource(bImg.getSource(),new MyFilter(w,h,hist)));
        initHistogramData();
        int i;
    }

    public void drawHistagram() {
        System.out.println(Arrays.toString(hist));
        getGraphics().setColor(Color.WHITE);
        getGraphics().drawRect(100,100,30,100);
        //repaint();
        System.out.println(Arrays.toString(histSorted));
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.drawImage(bImg,0,0,this);
        g.setColor(Color.white);
        int s=0;
        System.out.println("Höhe/2: "+h/2  + " SizeofCol: " +sizeOfCol  + " Höhe" + (-(h/100/2)*(hist[0]/prozent)+1) +" w : " + w);
        for(int i = 0 ; i<256;i++){
            g.fillRect(s, h-h/10, sizeOfCol, -(h/100/2)*(hist[i]/prozent)+1);
            s+=sizeOfCol+1;
        }
    }

    @Override
    public Dimension getPreferredSize() {
        int w = bImg.getWidth(this);
        int h = bImg.getHeight(this);
        if(w<512)w=512;
        return new Dimension(w,h);
    }

    static public void main(String ... args){
        final String[] s = new String[1];
        Frame f = new Frame("HISTOGRAM");
        Button b = new Button("open picture");
        FileDialog fileDialog = new FileDialog(f,"select a picture",FileDialog.LOAD);fileDialog.setDirectory("C:\\"); fileDialog.setFile("*.jpg");fileDialog.setVisible(true);
        s[0] = fileDialog.getDirectory()+fileDialog.getFile();
        System.out.println(s[0]);
        f.add(b);
        HistogramPicturePanel m = new HistogramPicturePanel(s[0]);
        f.add(m);
        f.pack();
        f.setVisible(true);
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                System.exit(0);
            }
        });
        m.drawHistagram();
    }
}
