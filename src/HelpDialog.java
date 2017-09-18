import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.JScrollPane;
import java.awt.CardLayout;
import javax.swing.ScrollPaneConstants;
import java.awt.Font;

public class HelpDialog extends JDialog {

	//variable
	private final JPanel contentPanel = new JPanel();
	private JLabel lblCommand;
	private JLabel lblShortcuts;
	private JLabel lblDescription;
	private JLabel lblCut;
	private JLabel lblOpenFile;
	private JLabel lblNewFolder;
	private JLabel lblNewFile;
	private JLabel lblDelete;
	private JLabel lblRename;
	private JLabel lblCopy;
	private JLabel lblOpenFolderShortCut;
	private JLabel lblOpenFileShortcut;
	private JLabel lblNewFolderShortcut;
	private JLabel lblNewFileShortcut;
	private JLabel lblDeleteShortcut;
	private JLabel lblRenameShortcut;
	private JLabel lblCopyShortcut;
	private JLabel lblOpenFolder;
	private JLabel lblOpenFolderDescription;
	private JLabel lblOpenFileDescription;
	private JLabel lblNewFolderDescription;
	private JLabel lblNewFileDescription;
	private JLabel lblDeleteDescription;
	private JLabel lblRenameDescription;
	private JLabel lblCopyDescription;
	private JLabel lblCutDescription;
	private JLabel lblPaste;
	private JLabel lblPasteDescription;
	private JLabel lblCutShortcut;
	private JLabel lblPasteShortcut;
	private JLabel lblExit;
	private JLabel lblExitShortcut;
	private JLabel lblExitDescription;



	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			HelpDialog dialog = new HelpDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);//create setDefaultCloseOperation dialog
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public HelpDialog() {
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JLabel lblHelp = new JLabel("Help(I)");
			lblHelp.setFont(new Font("Apple SD Gothic Neo", Font.BOLD, 20));
			contentPanel.add(lblHelp, BorderLayout.NORTH);
		}


		{

			///////set layout on dialog//////

			JPanel panel = new JPanel();
			panel.setBackground(Color.WHITE);
			JScrollPane scrollPane = new JScrollPane(panel);
			panel.setLayout(new GridLayout(0, 3, 0, 0));

			lblCommand = new JLabel("Command");
			lblCommand.setFont(new Font("Lucida Grande", Font.BOLD, 15));
			panel.add(lblCommand);

			lblShortcuts = new JLabel("Shortcuts");
			lblShortcuts.setFont(new Font("Lucida Grande", Font.BOLD, 15));
			panel.add(lblShortcuts);

			lblDescription = new JLabel("Description");
			lblDescription.setFont(new Font("Lucida Grande", Font.BOLD, 15));
			panel.add(lblDescription);

			lblOpenFolder = new JLabel("Open Folder");
			panel.add(lblOpenFolder);

			lblOpenFolderShortCut = new JLabel("CTRL+O");
			panel.add(lblOpenFolderShortCut);

			lblOpenFolderDescription = new JLabel("Open folder by dialog box");
			panel.add(lblOpenFolderDescription);

			lblOpenFile = new JLabel("Open file");
			panel.add(lblOpenFile);

			lblOpenFileShortcut = new JLabel("CTRL+Shift+O");
			panel.add(lblOpenFileShortcut);

			lblOpenFileDescription = new JLabel("Open file by dialog box");
			panel.add(lblOpenFileDescription);

			lblNewFolder = new JLabel("New folder");
			panel.add(lblNewFolder);

			lblNewFolderShortcut = new JLabel("CTRL+N");
			panel.add(lblNewFolderShortcut);

			lblNewFolderDescription = new JLabel("Create a folder");
			panel.add(lblNewFolderDescription);

			lblNewFile = new JLabel("New file");
			panel.add(lblNewFile);

			lblNewFileShortcut = new JLabel("CTRL+Shift+N");
			panel.add(lblNewFileShortcut);

			lblNewFileDescription = new JLabel("Create a file");
			panel.add(lblNewFileDescription);

			lblDelete = new JLabel("Delete");
			panel.add(lblDelete);

			lblDeleteShortcut = new JLabel("delete");
			panel.add(lblDeleteShortcut);

			lblDeleteDescription = new JLabel("delete folder/file");
			panel.add(lblDeleteDescription);

			lblRename = new JLabel("Rename");
			panel.add(lblRename);

			lblRenameShortcut = new JLabel("CTRL+R");
			panel.add(lblRenameShortcut);

			lblRenameDescription = new JLabel("Renaming the folder/file");
			panel.add(lblRenameDescription);

			lblCopy = new JLabel("Copy");
			panel.add(lblCopy);

			lblCopyShortcut = new JLabel("CTRL+C");
			panel.add(lblCopyShortcut);

			lblCopyDescription = new JLabel("Copy the folder/file");
			panel.add(lblCopyDescription);

			lblCut = new JLabel("Cut");
			panel.add(lblCut);

			lblCutShortcut = new JLabel("CTRL+X");
			panel.add(lblCutShortcut);

			lblCutDescription = new JLabel("Cut the folder/file");
			panel.add(lblCutDescription);

			lblPaste = new JLabel("Paste");
			panel.add(lblPaste);

			lblPasteShortcut = new JLabel("CTRL+V");
			panel.add(lblPasteShortcut);

			lblPasteDescription = new JLabel("Paste the folder/file");
			panel.add(lblPasteDescription);

			lblExit =  new  JLabel("Exit");
			panel.add(lblExit);

			lblExitShortcut =  new   JLabel("CTRL+Q");
			panel.add(lblExitShortcut);

			lblExitDescription =  new JLabel("Quit the FileManager Program");
			panel.add(lblExitDescription);


			contentPanel.add(scrollPane, BorderLayout.CENTER);

			////////////////////////////////////////////////////////


		}
		{

			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);

			{

				//add cancel button. if cancel button clicked, close dialog
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}});
			}
		}
	}
}
