package formulas;

import java.util.*;
import java.util.regex.*;

import formulas.Formula.Fields;
import java.awt.EventQueue;

import java.awt.event.*;

import javax.swing.GroupLayout.Alignment;
import javax.swing.table.DefaultTableModel;
import javax.swing.*;
import javax.swing.event.*;

public class DBEditor {

	private JFrame frame;
	private JTable table;
	private FormulaManager fm;
	ListSelectionModel s_model;
	Integer changed_row;
	List<Integer> changed_rows;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DBEditor window = new DBEditor();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public DBEditor() {
		initialize();
	}

	private Object [][] init_data() {
		fm = FormulaManager.initList();
		List<Formula> l = fm.listFormulas();
		
		Object [][] model_data = new Object [l.size()+1][4];
		int i = 0,j = 0;
		for (Formula item : l) {
			model_data[i][Fields.Id.getValue()] = item.getId();
			model_data[i][Fields.TeX.getValue()] = item.getFormulaTex();
			model_data[i][Fields.Page.getValue()] = item.getPageNum();
			model_data[i][Fields.Result.getValue()] = item.getResultSymbol();
			i+=1;
		}
		for (j=0; j<4; j++)
			model_data[i][j] = null; 
		return model_data;
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		Object [][]m = init_data();
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		table = new JTable(m, new String[] {
				"Id", "TeX", "Page", "Result"
			});
		table.setCellSelectionEnabled(true);
		s_model = table.getSelectionModel();
		changed_row = null;
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		InputMap im_table = table.getInputMap();
		
		im_table.put(KeyStroke.getKeyStroke("DOWN"), "DOWN");
		im_table.put(KeyStroke.getKeyStroke("UP"), "UP");

		ActionMap am_table = table.getActionMap();

		am_table.put("DOWN", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				System.out.println("Changing Row!");
				if (table.getSelectedRow() < table.getRowCount() - 1) {
					table.changeSelection(table.getSelectedRow() + 1, 0, false, false);
					changed_row = table.getSelectedRow();
				}
			}
		});
		am_table.put("UP", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				System.out.println("Changing Row!");
				if (table.getSelectedRow() > 0) {
					table.changeSelection(table.getSelectedRow() - 1, 0, false, false);
					changed_row = table.getSelectedRow();
				}
			}
		});

		System.out.println(table.getMouseListeners());
		for (MouseListener listener : table.getMouseListeners()) {
			table.removeMouseListener(listener);
		}
		for (MouseMotionListener listener : table.getMouseMotionListeners()) {
			table.removeMouseMotionListener(listener);
		}

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JTable table_source = (JTable)e.getSource();
				int rowAtPoint = table_source.rowAtPoint(e.getPoint());
				int columnAtPoint = table_source.columnAtPoint(e.getPoint());
				if (rowAtPoint != -1 && rowAtPoint != table.getSelectedRow()) {
					System.out.println("Changing Row!");
				
					table_source.changeSelection(rowAtPoint, columnAtPoint, false, false);
					changed_row = rowAtPoint;
				}
			}
		});
		table.changeSelection(0, 0, false, false);

		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(21)
					.addComponent(table, GroupLayout.PREFERRED_SIZE, 407, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(22, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(21)
					.addComponent(table, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(21, Short.MAX_VALUE))
		);
		frame.getContentPane().setLayout(groupLayout);
		
		JMenuBar menuBar = new JMenuBar();
		JMenu m0 = new JMenu("Operations");
		JMenuItem mi_ins = new JMenuItem("Insert");
		mi_ins.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (changed_row == table.getRowCount()-1) {
					fm.insertFormula((String)table.getValueAt(changed_row, Fields.TeX.getValue()),
							Integer.parseInt((String)table.getValueAt(changed_row, Fields.Page.getValue())));
				}
			}
		});
		JMenuItem mi_upd = new JMenuItem("Update");
		mi_upd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if ((changed_row != null) && (changed_row < table.getRowCount()-1)) {
					String symbol_regex = "(\\w)";
					Pattern r = Pattern.compile(symbol_regex);
					Matcher m = r.matcher(table.getValueAt(changed_row, Fields.TeX.getValue()).toString());
					List<Integer> symbols_to_update = new ArrayList<Integer>();
					while (m.find()){
						System.out.println("Found symbol " + m.group(0));
						List<Symbol> stored_symbol = fm.find_symbol(m.group(0));
						System.out.println("Already stored symbols: " + stored_symbol.size());
						if (stored_symbol.size() > 0) {
							fm.updateSymbol(stored_symbol.get(0).getId(),
									(Integer)table.getValueAt(changed_row, Fields.Id.getValue()));
							symbols_to_update.add(stored_symbol.get(0).getId());
						} else {
							Integer symb_id = fm.insertSymbol(m.group(0));
							symbols_to_update.add(symb_id);
						}
					}
					fm.updateFormula((Integer)table.getValueAt(changed_row, Fields.Id.getValue()),
							table.getValueAt(changed_row, Fields.TeX.getValue()).toString(),
							Integer.parseInt(table.getValueAt(changed_row, Fields.Page.getValue()).toString()),
							symbols_to_update);
				}
			}
		});
		JMenuItem mi_del = new JMenuItem("Delete");
		mi_del.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if ((changed_row != null) && (changed_row < table.getRowCount()-1)) {
					fm.deleteFormula((Integer)table.getValueAt(changed_row, Fields.Id.getValue()));
				}
			}
		});
		m0.add(mi_ins);
		m0.add(mi_upd);
		m0.add(mi_del);
		menuBar.add(m0);
		frame.setJMenuBar(menuBar);
	}
}
