
import java.io.File;
import java.io.PrintStream;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.AbstractTableModel;

/**
 * Detail view
 */
class FileTableModel extends AbstractTableModel
{
	private File[] files;
	private FileSystemView fileSystemView = FileSystemView.getFileSystemView();
	private String[] columns = { "Icon", "File", "Path/name", "Size", "Last Modified"};

	/**
	 * Constructor
	 */
	FileTableModel()
	{
		this(new File[0]);
	}

	/**
	 * Constructor(file)
	 * @param files
	 */
	FileTableModel(File[] files) {
		this.files = files;
	}


	public Object getValueAt(int row, int column) {
		File file = this.files[row];
		switch (column) {
		case 0:
			return this.fileSystemView.getSystemIcon(file);
		case 1:
			return this.fileSystemView.getSystemDisplayName(file);
		case 2:
			return file.getPath();
		case 3:
			return Long.valueOf(file.length());
		case 4:
			return Long.valueOf(file.lastModified());
		case 5:
			return Boolean.valueOf(file.canRead());
		case 6:
			return Boolean.valueOf(file.canWrite());
		case 7:
			return Boolean.valueOf(file.canExecute());
		case 8:
			return Boolean.valueOf(file.isDirectory());
		case 9:
			return Boolean.valueOf(file.isFile());
		}
		System.err.println("Logic Error");

		return "";
	}

	// getColumn

	public int getColumnCount() {
		return this.columns.length;
	}

	public Class<?> getColumnClass(int column) {
		switch (column) {
		case 0:
			return ImageIcon.class;
		case 3:
			return Long.class;
		case 4:
			return Date.class;
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
			return Boolean.class;
		case 1:
		case 2: } return String.class;
	}

	
	// getColName

	public String getColumnName(int column) {
		return this.columns[column];
	}

	//getRow
	public int getRowCount() {
		return this.files.length;
	}

	
	// Display detail information about a file
	public File getFile(int row) {
		return this.files[row];
	}

	public void setFiles(File[] files) {
		this.files = files;
		fireTableDataChanged();
	}
}