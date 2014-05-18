package initial;

import javax.swing.JTextArea;

public class Messenger {
    
    private JTextArea jTextArea;

    public Messenger(JTextArea jTextArea) {
        this.jTextArea = jTextArea;
    }
    
    public int show(String msg) {
        
        jTextArea.append("\n" + msg);
        return 1;
    }

}
