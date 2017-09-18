
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Container;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.*;
import java.awt.image.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.table.*;
import javax.swing.filechooser.FileSystemView;

import javax.imageio.ImageIO;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import java.io.*;
import java.nio.channels.FileChannel;

import java.net.URL;
import java.awt.CardLayout;
import java.awt.Color;


//FileManagerGUI
public class FileManagerGUI {

	
	//variable
	public static final String APP_TITLE = "FileManager";
	private static JLabel lbltreepath;
	private static Desktop desktop;
	private FileSystemView fileSystemView;
	private File currentFile;
	private static JPanel gui;
	private JTree tree;
	private DefaultTreeModel treeModel;
	private JTable table;
	private JList list;
	private JProgressBar progressBar;
	private FileTableModel fileTableModel;
	private ListSelectionListener listSelectionListener;
	private boolean cellSizesSet = false;
	private int rowIconPadding = 6;
	public static File willcopy;
	public static File pastefile;
	public static boolean copyFlag = false;
	public static boolean cutFlag = false;
	private JLabel fileName;
	private JTextField path;
	private JLabel date;
	private JLabel size;
	private JRadioButton isDirectory;
	private JRadioButton isFile;

	
	private JPanel newFilePanel;
	private JRadioButton newTypeFile;
	private JTextField name;
	private static JMenuBar menuBar;
	private static JMenu mnFile;
	private static JMenu mnEdit;
	private static JMenu mnView;
	private static JMenu mnHelp;
	
	private static JMenuItem mntmOpenFile;
	private static JMenuItem mntmOpenFolder;
	private static JMenuItem newfile;
	private static JMenuItem newfolder;
	private static JMenuItem delete;
	private static JMenuItem exit;
	private static CardLayout cards;
	private static JPanel detailView;
	private static JScrollPane tableScroll;
	private static JScrollPane listScroll;
	private static JScrollPane iconScroll;
	private static JScrollPane tileScroll;
	FileList fl;
	Fileicon fi;
	Filetile ft;
	
	//main
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				JFrame f = new JFrame(APP_TITLE);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				FileManagerGUI fileManager = new FileManagerGUI();

				menuBar = new JMenuBar();
				f.setJMenuBar(menuBar);
				setMenuBar(fileManager);

				f.setContentPane(fileManager.getGui());

				try {
					URL urlBig = fileManager.getClass().getResource("fm-icon-32x32.png");
					URL urlSmall = fileManager.getClass().getResource("fm-icon-16x16.png");
					ArrayList<Image> images = new ArrayList<Image>();
					images.add(ImageIO.read(urlBig));
					images.add(ImageIO.read(urlSmall));
					f.setIconImages(images);
				} catch (Exception weTried) {
				}

				f.pack();
				f.setLocationByPlatform(true);
				f.setSize(1000, 600);
				f.setVisible(true);

				fileManager.showRootFile();
			}
		});
	}
	
	//container
	public Container getGui() {
		
		if (gui == null) {


			gui = new JPanel(new BorderLayout(3, 3));
			gui.setBorder(new EmptyBorder(5, 5, 5, 5));
			
			
			// set gui layout
			fileSystemView = FileSystemView.getFileSystemView();
			desktop = Desktop.getDesktop();


			//create table
			table = new JTable();
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			table.setAutoCreateRowSorter(true);
			table.setShowVerticalLines(false);


			//listSelectionListener
			listSelectionListener = new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent lse) {
					int row = table.getSelectionModel().getLeadSelectionIndex();
					setFileDetails(((FileTableModel) table.getModel()).getFile(row));
				}
			};

			
			//addListSelectionListener 
			table.getSelectionModel().addListSelectionListener(listSelectionListener);

			tableScroll = new JScrollPane(table);
			Dimension d = tableScroll.getPreferredSize();
			
			//list
			fl = new FileList();
			listScroll = new JScrollPane(fl.jsp);
			Dimension ld = listScroll.getPreferredSize();

			fi = new Fileicon();
			iconScroll = new JScrollPane(fi.jsp);
			Dimension icd = iconScroll.getPreferredSize();

			ft = new Filetile();
			tileScroll = new JScrollPane(ft.jsp);
			Dimension td = tileScroll.getPreferredSize();

			detailView = new JPanel();
			cards = new CardLayout();
			detailView.setLayout(cards);

			// detail
			tableScroll.setPreferredSize(new Dimension((int) d.getWidth(), (int) d.getHeight() / 2));
			detailView.add("card_detail", tableScroll);
			
			
			// add list in card
			listScroll.setPreferredSize(new Dimension((int) ld.getWidth(), (int) ld.getHeight() / 2));
			detailView.add("card_list", listScroll);

			iconScroll.setPreferredSize(new Dimension((int) d.getWidth(), (int) d.getHeight() / 2));
			detailView.add("card_icon", iconScroll);
			
			// add detail in card
			tileScroll.setPreferredSize(new Dimension((int) ld.getWidth(), (int) ld.getHeight() / 2));
			detailView.add("card_tile", tileScroll);

			// the File tree
			DefaultMutableTreeNode root = new DefaultMutableTreeNode();
			treeModel = new DefaultTreeModel(root);
			TreeSelectionListener treeSelectionListener = new TreeSelectionListener() {
				public void valueChanged(TreeSelectionEvent tse) {

					DefaultMutableTreeNode node = (DefaultMutableTreeNode) tse.getPath().getLastPathComponent();
					showChildren(node);
					lbltreepath.setText(((File) node.getUserObject()).getPath());
					setFileDetails((File) node.getUserObject());
				}
			};

			// show the file system roots.
			File[] roots = fileSystemView.getRoots();
			for (File fileSystemRoot : roots) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileSystemRoot);
				root.add(node);
				// showChildren(node);
				//
				File[] files = fileSystemView.getFiles(fileSystemRoot, true);
				for (File file : files) {
					if (file.isDirectory()) {
						node.add(new DefaultMutableTreeNode(file));
					}
				}
				
			}

			tree = new JTree(treeModel);
			tree.setRootVisible(false);
			tree.addTreeSelectionListener(treeSelectionListener);
			tree.setCellRenderer(new FileTreeCellRenderer());
			tree.expandRow(0);
			JScrollPane treeScroll = new JScrollPane(tree);

			// as per trashgod tip
			tree.setVisibleRowCount(15);

			Dimension preferredSize = treeScroll.getPreferredSize();
			Dimension widePreferred = new Dimension(200, (int) preferredSize.getHeight());
			treeScroll.setPreferredSize(widePreferred);

			// details for a File
			JPanel fileMainDetails = new JPanel(new BorderLayout(4, 2));
			fileMainDetails.setBorder(new EmptyBorder(0, 6, 0, 6));

			JPanel fileDetailsLabels = new JPanel(new GridLayout(0, 1, 2, 2));
			fileMainDetails.add(fileDetailsLabels, BorderLayout.WEST);

			JPanel fileDetailsValues = new JPanel(new GridLayout(0, 1, 2, 2));
			fileMainDetails.add(fileDetailsValues, BorderLayout.CENTER);

			
			
			//detailView set
			fileDetailsLabels.add(new JLabel("File", JLabel.TRAILING));
			fileName = new JLabel();
			fileDetailsValues.add(fileName);
			fileDetailsLabels.add(new JLabel("Path/name", JLabel.TRAILING));
			path = new JTextField(5);
			path.setEditable(false);
			fileDetailsValues.add(path);
			fileDetailsLabels.add(new JLabel("Last Modified", JLabel.TRAILING));
			date = new JLabel();
			fileDetailsValues.add(date);
			fileDetailsLabels.add(new JLabel("File size", JLabel.TRAILING));
			size = new JLabel();
			fileDetailsValues.add(size);
			fileDetailsLabels.add(new JLabel("Type", JLabel.TRAILING));

			JPanel flags = new JPanel(new FlowLayout(FlowLayout.LEADING, 4, 0));
			isDirectory = new JRadioButton("Directory");
			isDirectory.setEnabled(false);
			flags.add(isDirectory);

			isFile = new JRadioButton("File");
			isFile.setEnabled(false);
			flags.add(isFile);
			fileDetailsValues.add(flags);

			int count = fileDetailsLabels.getComponentCount();
			for (int ii = 0; ii < count; ii++) {
				fileDetailsLabels.getComponent(ii).setEnabled(false);
			}

			JToolBar toolBar = new JToolBar();
			// mnemonics stop working in a floated toolbar
			toolBar.setFloatable(false);

			JPanel fileView = new JPanel(new BorderLayout(3, 3));

			fileView.add(toolBar, BorderLayout.NORTH);
			fileView.add(fileMainDetails, BorderLayout.CENTER);

			JSplitPane splitPane_1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, detailView, fileView);
			JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScroll, splitPane_1);

			gui.add(splitPane, BorderLayout.CENTER);

			JPanel simpleOutput = new JPanel(new BorderLayout(3, 3));
			progressBar = new JProgressBar();
			simpleOutput.add(progressBar, BorderLayout.EAST);
			progressBar.setVisible(false);

			gui.add(simpleOutput, BorderLayout.SOUTH);


			JPanel pathpanel = new JPanel();
			gui.add(pathpanel, BorderLayout.NORTH);
			pathpanel.setLayout(new GridLayout(0, 1, 0, 0));

			lbltreepath = new JLabel("");
			lbltreepath.setHorizontalAlignment(SwingConstants.LEFT);
			pathpanel.add(lbltreepath);


		}
		return gui;
	}

	public void showRootFile() {
		// ensure the main files are displayed
		tree.setSelectionInterval(0, 0);
	}

	private TreePath findTreePath(File find) {
		for (int ii = 0; ii < tree.getRowCount(); ii++) {

			TreePath treePath = tree.getPathForRow(ii);

			Object object = treePath.getLastPathComponent();
			// System.out.println(" " + ii + treePath + "glpc ");
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) object;
			File nodeFile = (File) node.getUserObject();

			if (("" + nodeFile).equals("" + find)) {
				return treePath;
			}
		}
		// not found!
		return null;
	}
	
	
	/*
	 * copy function
	 */

	public void copyFolder(File source, File destination) {
		if (source.isDirectory()) {
			if (!destination.exists()) {
				destination.mkdirs();
			}
			String files[] = source.list();

			for (String file : files) {
				File srcFile = new File(source, file);
				File destFile = new File(destination, file);

				copyFolder(srcFile, destFile);
			}
		} else {
			InputStream in = null;
			OutputStream out = null;

			try {
				// use InputStream, OutputStream
				in = new FileInputStream(source);
				out = new FileOutputStream(destination);

				byte[] buffer = new byte[1024];

				int length;
				while ((length = in.read(buffer)) > 0) {
					out.write(buffer, 0, length);
				}
				in.close();
				out.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
	
	//renameFile function

	private void renameFile() {
		if (currentFile==null) {
			showErrorMessage("No file selected to rename.","Select File");
			return;
		}

		String renameTo = JOptionPane.showInputDialog(gui, "New Name");
		if (renameTo!=null) {
			try {
				boolean directory = currentFile.isDirectory();
				TreePath parentPath = findTreePath(currentFile.getParentFile());
				DefaultMutableTreeNode parentNode =
						(DefaultMutableTreeNode)parentPath.getLastPathComponent();

				boolean renamed = currentFile.renameTo(new File(
						currentFile.getParentFile(), renameTo));
				if (renamed) {
					if (directory) {
						// rename the node..

						// delete the current node..
						TreePath currentPath = findTreePath(currentFile);
						System.out.println(currentPath);
						DefaultMutableTreeNode currentNode =
								(DefaultMutableTreeNode)currentPath.getLastPathComponent();

						treeModel.removeNodeFromParent(currentNode);

						// add a new node..
					}

					showChildren(parentNode);
				} else {
					String msg = "The file '" +
							currentFile +
							"' could not be renamed.";
					showErrorMessage(msg,"Rename Failed");
				}
			} catch(Throwable t) {
				showThrowable(t);
			}
		}
		gui.repaint();
	}

	
	/*
	 * delete file
	 */
	private void deleteFile() {
		if (currentFile == null) {
			showErrorMessage("No file selected for deletion.", "Select File");
			return;
		}

		int result = JOptionPane.showConfirmDialog(gui,
				"Are you sure you want to delete " + currentFile.getName() + " ?", "Delete File",
				JOptionPane.ERROR_MESSAGE);
		if (result == JOptionPane.OK_OPTION) {//if user select ok button,
			try {
				System.out.println("currentFile: " + currentFile);

				TreePath parentPath = findTreePath(currentFile.getParentFile());
				System.out.println(currentFile.getParentFile());
				System.out.println("parentPath: " + parentPath);
				DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parentPath.getLastPathComponent();
				System.out.println("parentNode: " + parentNode);

				boolean directory = currentFile.isDirectory();
				if (directory) {
					deleteFolder(currentFile);
					return;
				}
				boolean deleted = currentFile.delete();
				System.out.println(currentFile);
				if (deleted) {
					if (directory) {
						// delete the node..
						if(currentFile == null)
							return;
						TreePath currentPath = findTreePath(currentFile);
						System.out.println(currentPath);
						DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) currentPath
								.getLastPathComponent();

						treeModel.removeNodeFromParent(currentNode);
					}

					showChildren(parentNode);
				} else {
					String msg = "The file '" + currentFile + "' could not be deleted.";
					showErrorMessage(msg, "Delete Failed");
				}
			} catch (Throwable t) {
				showThrowable(t);
			}
		}
		gui.repaint();
		path.setText(currentFile.getPath());
	}

	
	//deleteFolder function
	private void deleteFolder(File currentFile) {
		if (currentFile == null) {
			showErrorMessage("No file selected for deletion.", "Select File");
			return;
		}

		try {
			System.out.println("currentFile: " + currentFile);

			TreePath parentPath = findTreePath(currentFile.getParentFile());
			System.out.println(currentFile.getParentFile());
			System.out.println("parentPath: " + parentPath);
			DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parentPath.getLastPathComponent();
			System.out.println("parentNode: " + parentNode);

			boolean directory = currentFile.isDirectory();

			if (directory) {

				File[] childFile = currentFile.listFiles();
				int size = childFile.length;

				if (size > 0) {
					for (int i = 0; i < size; i++) {
						if (childFile[i].isFile()) {
							childFile[i].delete();
						} else {
							deleteFolder(childFile[i]);
						}
					}
				}
				// delete the node..
				System.out.println(currentFile);
				if (currentFile == null)
					return;
				TreePath currentPath = findTreePath(currentFile);
				DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) currentPath.getLastPathComponent();

				treeModel.removeNodeFromParent(currentNode);
			}

			showChildren(parentNode);

			currentFile.delete();
		} catch (Throwable t) {
			showThrowable(t);
		}

		gui.repaint();
		path.setText(currentFile.getPath());
	}
	
	//updatelist
	private void updatelist(){
		fl = new FileList();
	}
	
	//newFile function
	private void newFile(int flag) {
		if (currentFile == null) {
			showErrorMessage("No location selected for new file.", "Select Location");
			return;
		}

		if (newFilePanel == null) {
			newFilePanel = new JPanel(new BorderLayout(3, 3));

			JPanel southRadio = new JPanel(new GridLayout(1, 0, 2, 2));


			name = new JTextField(15);

			newFilePanel.add(new JLabel("Name"), BorderLayout.WEST);
			newFilePanel.add(name);
			newFilePanel.add(southRadio, BorderLayout.SOUTH);
		}

		
		/*
		 * create file function
		 */
		int result = JOptionPane.showConfirmDialog(gui, newFilePanel, "Create File", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			try {
				boolean created;
				File parentFile = currentFile;
				if (!parentFile.isDirectory()) {
					parentFile = parentFile.getParentFile();
				}
				File file = new File(parentFile, name.getText());
				if (flag == 0) {
					created = file.createNewFile();
				} else {
					created = file.mkdir();
				}
				if (created) {

					TreePath parentPath = findTreePath(parentFile);
					DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parentPath.getLastPathComponent();

					if (file.isDirectory()) {
						// add the new node..
						DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(file);

						TreePath currentPath = findTreePath(currentFile);
						DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) currentPath
								.getLastPathComponent();

						treeModel.insertNodeInto(newNode, parentNode, parentNode.getChildCount());
					}

					showChildren(parentNode);
				} else {
					String msg = "The file '" + file + "' could not be created.";
					showErrorMessage(msg, "Create Failed");
				}
			} catch (Throwable t) {
				showThrowable(t);
			}
		}
		gui.repaint();
	}

	
	/*
	 * paste function
	 */
	private void pastefile() {
		SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
			public Void doInBackground() {
				TreePath currentPath = findTreePath(currentFile);
				System.out.println(currentPath);
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) currentPath.getLastPathComponent();
				File file = (File) selectedNode.getUserObject();
				pastefile = new File(file.getAbsolutePath() + "/" + willcopy.getName());
				System.out.println(willcopy + " " + file + " " + pastefile);
				try {
					if ((copyFlag == false && cutFlag == false))

						System.out.println("you didn't copy or paste");
					if (copyFlag == true) {// copyflag is true, copy func run
						copyFolder(willcopy, pastefile);
						copyFlag = false;
					}
					if (cutFlag == true && !willcopy.renameTo(pastefile)) {

						copyFolder(willcopy, pastefile);
						currentFile = willcopy;
						deleteFile();
					}

				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				// tree update
				addChildeNode(selectedNode);
				treeModel.nodeChanged(selectedNode);
				treeModel.nodeStructureChanged(selectedNode);
				treeModel.reload(selectedNode);

				return null;
			}
		};
		// use thread.
		worker.execute();
	}

	
	
	private void showErrorMessage(String errorMessage, String errorTitle) {
		JOptionPane.showMessageDialog(gui, errorMessage, errorTitle, JOptionPane.ERROR_MESSAGE);
	}

	private void showThrowable(Throwable t) {
		t.printStackTrace();
		JOptionPane.showMessageDialog(gui, t.toString(), t.getMessage(), JOptionPane.ERROR_MESSAGE);
		gui.repaint();
	}

	/** Update the table on the EDT */

	private void addChildeNode(DefaultMutableTreeNode node) {
		tree.setEnabled(false);
		SwingWorker<Void, File> worker = new SwingWorker<Void, File>() {
			@Override

			// thread works InBackground.
			public Void doInBackground() {
				File file = (File) node.getUserObject();
				if (file.isDirectory()) {// file is directory,
					File[] files = fileSystemView.getFiles(file, true);
					if (node.isLeaf()) {
						for (File child : files) {
							if (child.isDirectory()) {
								publish(child);// call process function with
								// child parameter
							}
						}
					}
					setTableData(files);
				}
				return null;
			}

			@Override
			// add childe at node
			protected void process(List<File> chunks) {
				for (File child : chunks) {
					node.add(new DefaultMutableTreeNode(child));
				}
			}

			@Override
			// worker is done, done function run.
			protected void done() {
				// progressBar.setIndeterminate(false);
				// progressBar.setVisible(false);
				tree.setEnabled(true);
			}
		};
		worker.execute();// use thread.
	}
	
	
	/*setTable date
	 * 
	 */

	private void setTableData(final File[] files) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (fileTableModel == null) {
					fileTableModel = new FileTableModel();
					table.setModel(fileTableModel);
				}
				table.getSelectionModel().removeListSelectionListener(listSelectionListener);
				fileTableModel.setFiles(files);
				table.getSelectionModel().addListSelectionListener(listSelectionListener);
				if (!cellSizesSet) {
					Icon icon = fileSystemView.getSystemIcon(files[0]);

					// size adjustment to better account for icons
					table.setRowHeight(icon.getIconHeight() + rowIconPadding);

					setColumnWidth(0, -1);
					setColumnWidth(3, 60);
					table.getColumnModel().getColumn(3).setMaxWidth(120);
					setColumnWidth(4, -1);

					cellSizesSet = true;
				}
			}
		});
	}

	private void setColumnWidth(int column, int width) {
		TableColumn tableColumn = table.getColumnModel().getColumn(column);
		if (width < 0) {
			// use the preferred width of the header..
			JLabel label = new JLabel((String) tableColumn.getHeaderValue());
			Dimension preferred = label.getPreferredSize();
			width = (int) preferred.getWidth() + 14;
		}
		tableColumn.setPreferredWidth(width);
		tableColumn.setMaxWidth(width);
		tableColumn.setMinWidth(width);
	}

	/**
	 * showChildren function 
	 * 
	 */
	private void showChildren(final DefaultMutableTreeNode node) {
		tree.setEnabled(false);
		progressBar.setVisible(true);
		progressBar.setIndeterminate(true);

		SwingWorker<Void, File> worker = new SwingWorker<Void, File>() {
			@Override
			public Void doInBackground() {
				File file = (File) node.getUserObject();
				if (file.isDirectory()) {
					File[] files = fileSystemView.getFiles(file, true); // !!
					if (node.isLeaf()) {
						for (File child : files) {
							if (child.isDirectory()) {
								publish(child);
							}
						}
					}
					setTableData(files);
				}
				return null;
			}

			@Override
			protected void process(List<File> chunks) {
				for (File child : chunks) {
					node.add(new DefaultMutableTreeNode(child));
				}
			}

			@Override
			protected void done() {
				progressBar.setIndeterminate(false);
				progressBar.setVisible(false);
				tree.setEnabled(true);
			}
		};
		worker.execute();
	}

	/** Update the File details view with the details of this File. */
	private void setFileDetails(File file) {
		currentFile = file;
		Icon icon = fileSystemView.getSystemIcon(file);
		fileName.setIcon(icon);
		fileName.setText(fileSystemView.getSystemDisplayName(file));
		path.setText(file.getPath());
		date.setText(new Date(file.lastModified()).toString());
		size.setText(file.length() + " bytes");

		isDirectory.setSelected(file.isDirectory());

		isFile.setSelected(file.isFile());

		JFrame f = (JFrame) gui.getTopLevelAncestor();
		if (f != null) {
			f.setTitle(APP_TITLE + " :: " + fileSystemView.getSystemDisplayName(file));
		}

		gui.repaint();
	}

	public static boolean copyFile(File from, File to) throws IOException {

		boolean created = to.createNewFile();

		if (created) {
			FileChannel fromChannel = null;
			FileChannel toChannel = null;
			try {
				fromChannel = new FileInputStream(from).getChannel();
				toChannel = new FileOutputStream(to).getChannel();

				toChannel.transferFrom(fromChannel, 0, fromChannel.size());


			} finally {
				if (fromChannel != null) {
					fromChannel.close();
				}
				if (toChannel != null) {
					toChannel.close();
				}
				return false;
			}
		}
		return created;
	}

	
	/*
	 * cut File function
	 */
	public static void cutFile(File source, File destination) {
		byte[] buf = new byte[1024];
		FileInputStream fin = null;
		FileOutputStream fout = null;

		try {


			fin = new FileInputStream(source);
			fout = new FileOutputStream(destination);

			int read = 0;
			while ((read = fin.read(buf, 0, buf.length)) != -1)
				fout.write(buf, 0, read);

			fin.close();
			fout.close();

			source.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	
	//menu setting
	static void setMenuBar(FileManagerGUI fileManager) {

		JMenu mnFile = new JMenu("File");
		JMenu mnEdit = new JMenu("Edit");
		JMenu mnView = new JMenu("View");
		JMenu mnHelp = new JMenu("Help");
		JMenuItem itemfdopen = new JMenuItem("Open Folder");
		JMenuItem itemfopen = new JMenuItem("Open File");
		JMenuItem itemnfd = new JMenuItem("New Folder");
		JMenuItem itemnf = new JMenuItem("New File");
		JMenuItem itemdelete = new JMenuItem("Delete");
		JMenuItem itemexit = new JMenuItem("Exit");
		JMenuItem itemrename = new JMenuItem("Rename");
		JMenuItem itemcopy = new JMenuItem("Copy");
		JMenuItem itemcut = new JMenuItem("Cut");
		JMenuItem itempaste = new JMenuItem("Paste");
		JMenuItem itemdetails = new JMenuItem("Details");
		JMenuItem itemlist = new JMenuItem("List");
		JMenuItem itemicons = new JMenuItem("Icons");
		JMenuItem itemtile = new JMenuItem("Titles");
		JMenuItem itemhelp = new JMenuItem("Help!");
		itemfdopen.setAccelerator(KeyStroke.getKeyStroke('O', Event.CTRL_MASK));
		itemfopen.setAccelerator(KeyStroke.getKeyStroke('O', (Event.CTRL_MASK | Event.SHIFT_MASK)));
		itemnfd.setAccelerator(KeyStroke.getKeyStroke('N', Event.CTRL_MASK));
		itemnf.setAccelerator(KeyStroke.getKeyStroke('N', (Event.CTRL_MASK | Event.SHIFT_MASK)));
		itemdelete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		itemrename.setAccelerator(KeyStroke.getKeyStroke('R', Event.CTRL_MASK));
		itemcopy.setAccelerator(KeyStroke.getKeyStroke('C', Event.CTRL_MASK));
		itemcut.setAccelerator(KeyStroke.getKeyStroke('X', Event.CTRL_MASK));
		itempaste.setAccelerator(KeyStroke.getKeyStroke('V', Event.CTRL_MASK));
		itemexit.setAccelerator(KeyStroke.getKeyStroke('Q', Event.CTRL_MASK));

		mnFile.add(itemfdopen);
		mnFile.add(itemfopen);
		mnFile.add(itemnfd);
		mnFile.add(itemnf);
		mnFile.add(itemdelete);
		mnFile.add(itemexit);
		mnEdit.add(itemrename);
		mnEdit.add(itemcopy);
		mnEdit.add(itemcut);
		mnEdit.add(itempaste);
		mnView.add(itemdetails);
		mnView.add(itemlist);
		mnView.add(itemicons);
		mnView.add(itemtile);
		mnHelp.add(itemhelp);

		
		/*
		 * file open addActionListener
		 */
		itemfopen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				try {
					JDialog.setDefaultLookAndFeelDecorated(true);
					JFileChooser fileChooser = new JFileChooser();
					// UIManager
					UIManager.put("FileChooser.openDialogTitleText", "Open File");
					UIManager.put("FileChooser.lookInLabelText", "Look In");
					UIManager.put("FileChooser.openButtonText", "Open");
					UIManager.put("FileChooser.cancelButtonText", "Cancel");
					UIManager.put("FileChooser.fileNameLabelText", "File name:");
					UIManager.put("FileChooser.filesOfTypeLabelText", "Files of Type:");
					SwingUtilities.updateComponentTreeUI(fileChooser);

					int returnValue = fileChooser.showOpenDialog(null);
					if (returnValue == JFileChooser.APPROVE_OPTION) {
						File selectedFile = fileChooser.getSelectedFile();
						desktop.open(selectedFile);
					}
				} catch (Throwable t) {

				}

			}
		});
		
		/*
		 * folder open addActionListener
		 */
		itemfdopen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				// JDialog.setDefaultLookAndFeelDecorated(true);
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileChooser.setCurrentDirectory(new File("user.home"));
				// UIManager
				UIManager.put("FileChooser.openDialogTitleText", "Open Folder");
				UIManager.put("FileChooser.lookInLabelText", "Look In");
				UIManager.put("FileChooser.openButtonText", "Open");
				UIManager.put("FileChooser.cancelButtonText", "Cancel");
				UIManager.put("FileChooser.fileNameLabelText", "Folder name:");
				UIManager.put("FileChooser.filesOfTypeLabelText", "Files of Type:");
				SwingUtilities.updateComponentTreeUI(fileChooser);

				int returnValue = fileChooser.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File selectedFolder = fileChooser.getCurrentDirectory();
					try {
						desktop.open(selectedFolder);
					} catch (Throwable t) {
						// showThrowable(t);
					}
				}
			}
		});

		
		/*
		 * new file addActionListener
		 */
		
		itemnf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				fileManager.newFile(0);
			}
		});
		
		/*
		 * new folder addActionListener
		 */
		itemnfd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				fileManager.newFile(1);
			}
		});
		
		/*
		 * delete addActionListener
		 */
		itemdelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				fileManager.deleteFile();
			}
		});
		
		/*
		 * exit addActionListener
		 */
		itemexit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				System.exit(0);
			}
		}); 
		
		/*
		 * rename addActionListener
		 */
		itemrename.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				fileManager.renameFile();
			}
		});
		/*
		 * copy addActionListener
		 */
		itemcopy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				willcopy = new File(fileManager.currentFile.getAbsolutePath());
				copyFlag = true;
				cutFlag = false;
			}
		});
		
		/*
		 * cut addActionListener
		 */
		itemcut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {

				willcopy = new File(fileManager.currentFile.getAbsolutePath());
				copyFlag = false;
				cutFlag = true;
			}
		});
		/*
		 * paste addActionListener
		 */
		itempaste.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				fileManager.pastefile();

			}

		});
		/*
		 * help addActionListener
		 */

		itemhelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//HelpDialog help = new HelpDialog();
				HelpDialog helpDialog =  new HelpDialog();
				System.gc();
				helpDialog.setVisible(true);
			}
		});

		/* 
		 * detail addActionListener
		 */
		itemdetails.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				cards.show(detailView, "card_detail");
			}
		});

		/*
		 * list addActionListener
		 */
		itemlist.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				fileManager.updatelist();
				cards.show(detailView, "card_list");
			}
		});
		/*
		 * icon addActionListener
		 */
		itemicons.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				fileManager.updatelist();
				cards.show(detailView, "card_icon");
			}
		});
		/*
		 * title addActionListener
		 */
		itemtile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				fileManager.updatelist();
				cards.show(detailView, "card_tile");
			}
		});

		//add
		menuBar.add(mnFile);
		menuBar.add(mnEdit);
		menuBar.add(mnView);
		menuBar.add(mnHelp);
	}

	
	
	protected File currentFile() {
		return null;
	}

	
	//list view 
	public class FileList {
		JScrollPane jsp;

		public FileList() {
			JPanel gui = new JPanel(new BorderLayout(2, 2));

			File userHome = new File(System.getProperty("user.home"));
			File[] files;
			if(currentFile != null)
				files = currentFile.listFiles();
			else
				files = userHome.listFiles();
			JList list = new JList(files);
			list.setCellRenderer(new FileListCellRenderer());
			jsp = new JScrollPane(list);
			
		}

	}

	/** A FileListCellRenderer for a File. */
	class FileListCellRenderer extends DefaultListCellRenderer {

		private static final long serialVersionUID = -7799441088157759804L;
		private FileSystemView fileSystemView;
		private JLabel label;
		private Color textSelectionColor = Color.BLACK;
		private Color backgroundSelectionColor = Color.CYAN;
		private Color textNonSelectionColor = Color.BLACK;
		private Color backgroundNonSelectionColor = Color.WHITE;

		FileListCellRenderer() {
			label = new JLabel();
			label.setOpaque(true);
			fileSystemView = FileSystemView.getFileSystemView();
		}

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean selected,
				boolean expanded) {

			File file = (File) value;
			label.setIcon(fileSystemView.getSystemIcon(file));
			label.setText(fileSystemView.getSystemDisplayName(file));
			label.setToolTipText(file.getPath());
			label.setSize(100, 100);
			if (selected) {
				label.setBackground(backgroundSelectionColor);
				label.setForeground(textSelectionColor);
			} else {
				label.setBackground(backgroundNonSelectionColor);
				label.setForeground(textNonSelectionColor);
			}

			return label;
		}
	}

	
	//icon view
	public class Fileicon {
		JScrollPane jsp;

		public Fileicon() {
			JPanel gui = new JPanel(new BorderLayout(2, 2));

			File userHome = new File(System.getProperty("user.home"));
			File[] files;
			if(currentFile != null)
				files = currentFile.listFiles();
			else
				files = userHome.listFiles();
			JList list = new JList(files);
			list.setCellRenderer(new FileiconCellRenderer());
			jsp = new JScrollPane(list);
			// JOptionPane.showMessageDialog(null, gui);
		}

	}

	/** A FileListCellRenderer for a File. */
	class FileiconCellRenderer extends DefaultListCellRenderer {

		private static final long serialVersionUID = -7799441088157759804L;
		private FileSystemView fileSystemView;
		private JLabel label;
		private Color textSelectionColor = Color.BLACK;
		private Color backgroundSelectionColor = Color.CYAN;
		private Color textNonSelectionColor = Color.BLACK;
		private Color backgroundNonSelectionColor = Color.WHITE;

		FileiconCellRenderer() {
			fileSystemView = FileSystemView.getFileSystemView();
		}




		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean selected,
				boolean expanded) {

			File file = (File) value;
			label = new JLabel(fileSystemView.getSystemDisplayName(file),fileSystemView.getSystemIcon(file),SwingConstants.CENTER);
			label.setOpaque(true);
			label.setToolTipText(file.getPath());
			label.setIconTextGap(20);
			label.setHorizontalTextPosition(JLabel.CENTER);
			label.setVerticalTextPosition(JLabel.BOTTOM);

			if (selected) {
				label.setBackground(backgroundSelectionColor);
				label.setForeground(textSelectionColor);
			} else {
				label.setBackground(backgroundNonSelectionColor);
				label.setForeground(textNonSelectionColor);
			}

			return label;
		}
	}

	//title view
	public class Filetile {
		JScrollPane jsp;

		public Filetile() {
			JPanel gui = new JPanel(new BorderLayout(2, 2));

			File userHome = new File(System.getProperty("user.home"));
			File[] files;
			if(currentFile != null)
				files = currentFile.listFiles();
			else
				files = userHome.listFiles();
			JList list = new JList(files);
			list.setCellRenderer(new FiletileCellRenderer());
			jsp = new JScrollPane(list);
			// JOptionPane.showMessageDialog(null, gui);
		}

	}

	/** A FileListCellRenderer for a File. */
	class FiletileCellRenderer extends DefaultListCellRenderer {

		private static final long serialVersionUID = -7799441088157759804L;
		private FileSystemView fileSystemView;
		private JLabel label;
		private Color textSelectionColor = Color.BLACK;
		private Color backgroundSelectionColor = Color.CYAN;
		private Color textNonSelectionColor = Color.BLACK;
		private Color backgroundNonSelectionColor = Color.WHITE;

		FiletileCellRenderer() {
			label = new JLabel();
			label.setOpaque(true);
			fileSystemView = FileSystemView.getFileSystemView();
		}

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean selected,
				boolean expanded) {

			File file = (File) value;
			label.setIcon(fileSystemView.getSystemIcon(file));
			label.setText(fileSystemView.getSystemDisplayName(file) + "  \n" + fileSystemView.getSystemTypeDescription(file));
			label.setToolTipText(file.getPath());
			label.setIconTextGap(20);

			if (selected) {
				label.setBackground(backgroundSelectionColor);
				label.setForeground(textSelectionColor);
			} else {
				label.setBackground(backgroundNonSelectionColor);
				label.setForeground(textNonSelectionColor);
			}

			return label;
		}
	}

	
	
}