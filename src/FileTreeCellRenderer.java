
import java.awt.Component;
import java.io.File;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;


//TreeCellRendeerder
class FileTreeCellRenderer extends DefaultTreeCellRenderer
{
	private FileSystemView fileSystemView;
	private JLabel label;

	
	/**
	 * Constructor init label
	 */
	FileTreeCellRenderer()
	{
		this.label = new JLabel();
		this.label.setOpaque(true);
		this.fileSystemView = FileSystemView.getFileSystemView();
	}

	
	/**
     * getTreeCellRendererComponent
     * return file's label
     * setIcon & settext
     * selected At tree => highlight Back,ForeGround
     */
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
	{
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
		File file = (File)node.getUserObject();
		this.label.setIcon(this.fileSystemView.getSystemIcon(file));
		this.label.setText(this.fileSystemView.getSystemDisplayName(file));
		this.label.setToolTipText(file.getPath());

		if (selected) {
			this.label.setBackground(this.backgroundSelectionColor);
			this.label.setForeground(this.textSelectionColor);
		} else {
			this.label.setBackground(this.backgroundNonSelectionColor);
			this.label.setForeground(this.textNonSelectionColor);
		}

		return this.label;
	}
}