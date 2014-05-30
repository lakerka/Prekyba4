package panels;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

//panel used to add components in vertical stack
public class StackPanel extends JPanel {
    

    /**
     * 
     */
    private static final long serialVersionUID = 4495689762445370605L;

    public StackPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    public void add(JComponent comp) {
        comp.setAlignmentX(0);
        super.add(comp);
    }

    @Override
    public Dimension getMaximumSize() {
        return super.getPreferredSize();
    }
}
