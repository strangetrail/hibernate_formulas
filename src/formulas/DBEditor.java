package formulas;

import java.util.*;
import java.util.regex.*;

import formulas.Formula.Fields;
import java.awt.EventQueue;

import java.awt.event.*;

import javax.swing.GroupLayout.Alignment;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.LayoutStyle.ComponentPlacement;

public class DBEditor {

	private JFrame frame;
	private JTable table;
	private JScrollPane jspTable;
	private FormulaManager fm;
	private ListSelectionModel s_model;
	private int changed_row;
	private int selected_row;
	private List<Integer> changed_rows;
	private TableModelListener tmlRefresh;
	private Object [][]m;
	private Object []m_columns;


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
		changed_rows = new ArrayList<Integer>();
		m = init_data();
		m_columns = new String[] {
				"Id", "TeX", "Page", "Result"
			};
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		table = new JTable(m, m_columns);
		table.setCellSelectionEnabled(true);
		s_model = table.getSelectionModel();
		changed_row = -1;
		selected_row = -1;
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tmlRefresh = new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				boolean already_listed = false;
				int row = e.getFirstRow();
				changed_row = row;
				for (int item : changed_rows) {
					System.out.println("Comparing " + item + " with " + changed_row);
					if (item == changed_row) {
						System.out.println("Found duplicate " + changed_row);
						already_listed = true;
						break;
					}
				}
				System.out.println("Duplicate status now is " + already_listed);
				if (!already_listed) {
					System.out.println("Adding changed row " + changed_row);
					changed_rows.add(changed_row);
				}
			}
		};
		table.getModel().addTableModelListener(tmlRefresh);
		
		
		
		InputMap inputMap = table.getInputMap();
		 
        inputMap.put(KeyStroke.getKeyStroke("DOWN"), "DOWN");
        inputMap.put(KeyStroke.getKeyStroke("UP"), "UP");
 
        ActionMap actionMap = table.getActionMap();
        actionMap.put("DOWN", new AbstractAction() {
 
            public void actionPerformed(ActionEvent e) {
                System.out.println("Changing Row!");
                if (table.getSelectedRow() < table.getRowCount() - 1) {
                    table.changeSelection(table.getSelectedRow() + 1, table.getSelectedColumn(), false, false);
                    selected_row = table.getSelectedRow();
                }
            }
        });
 
        actionMap.put("UP", new AbstractAction() {
 
            public void actionPerformed(ActionEvent e) {
                System.out.println("Changing Row!");
                if (table.getSelectedRow() > 0) {
                    table.changeSelection(table.getSelectedRow() - 1, table.getSelectedColumn(), false, false);
                    selected_row = table.getSelectedRow();
                }
            }
        });
 
        System.out.println(table.getMouseListeners());
        for (MouseListener listener : table.getMouseListeners()) {
            table.removeMouseListener(listener);
        }
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        for (MouseMotionListener listener : table.getMouseMotionListeners()) {
            table.removeMouseMotionListener(listener);
        }
         
        table.addMouseListener(new MouseAdapter() {
 
            @Override
            public void mouseClicked(MouseEvent e) {
                JTable table = (JTable)e.getSource();
 
                int rowAtPoint = table.rowAtPoint(e.getPoint());
                int columnAtPoint = table.columnAtPoint(e.getPoint());
                if (rowAtPoint != -1 && rowAtPoint != table.getSelectedRow()) {
                    System.out.println("Changing Row!");
                 
                    table.changeSelection(rowAtPoint, columnAtPoint, false, false);
                }
                if (rowAtPoint != -1 && rowAtPoint == table.getSelectedRow()) {
                	table.changeSelection(rowAtPoint, columnAtPoint, false, false);
                }
                selected_row = rowAtPoint;
            }
        });
		
		
		
		
		table.changeSelection(0, 0, false, false);
		jspTable = new JScrollPane(table);

		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(20)
					.addComponent(jspTable, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(23, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(jspTable, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(30, Short.MAX_VALUE))
		);
		frame.getContentPane().setLayout(groupLayout);
		
		JMenuBar menuBar = new JMenuBar();
		JMenu m0 = new JMenu("Operations");
		JMenu m1 = new JMenu("View");
		JMenuItem mi_refresh = new JMenuItem("Refresh");
		mi_refresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				m = init_data();
				table.setModel(new DefaultTableModel(m, m_columns));
				table.getModel().addTableModelListener(tmlRefresh);
			}
		});
		JMenuItem mi_ins = new JMenuItem("Insert");
		mi_ins.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Inserting on " + changed_row + " from " + table.getRowCount());
				int row_number = 0;
				boolean insertion_left = false;
				
				while (!insertion_left && (row_number < changed_rows.size())) {
						insertion_left = changed_rows.get(row_number) == (table.getRowCount()-1);
						if (insertion_left) {
							changed_row = changed_rows.get(row_number);
							System.out.println("Find insertion on " + row_number + " index");
						}
						row_number+=1;
				}
				if (row_number == changed_rows.size() && !insertion_left) {
					System.out.println("Cleaning last changed row number " + changed_row);
					changed_row = -1;
				}
				if (changed_row != -1) {
					if (changed_row == table.getRowCount()-1) {
						fm.insertFormula((String)table.getValueAt(changed_row, Fields.TeX.getValue()),
								Integer.parseInt((String)table.getValueAt(changed_row, Fields.Page.getValue())));
						changed_row = -1;
						changed_rows.remove(row_number-1);
					}
				}
			}
		});
		JMenuItem mi_upd = new JMenuItem("Update");
		mi_upd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int row_number;
				Integer symb_id;
				String found_result = null;
				int updateResult = -1;
				boolean insertion_left = false;
				while ((changed_rows.size() > 0) && !insertion_left) {
					row_number = changed_rows.size()-1;
					changed_row = changed_rows.get(row_number);
					if ((changed_row != -1) && (changed_row < table.getRowCount()-1)) {
						String symbol_regex = "([a-z]|[A-Z])";
						String result_regex = "^([A-Z]|[a-z])=";
						String formula_TeX = table.getValueAt(changed_row, Fields.TeX.getValue()).toString();
						Pattern r = Pattern.compile(symbol_regex);
						Pattern r_result = Pattern.compile(result_regex);
						Matcher m = r.matcher(formula_TeX);
						Matcher m_result = r_result.matcher(formula_TeX);
						List<Integer> symbols_to_update = new ArrayList<Integer>();
						List<Formula> result_list = fm.listFormulas(formula_TeX);
						System.out.println("Result list length is: " + result_list.size());
						if (m_result.find())
							found_result = m_result.group(1);
						else
							found_result = null;
						while (m.find()){
							System.out.println("Found symbol " + m.group(0));
							List<Symbol> stored_symbol = fm.find_symbol(m.group(0));
							System.out.println("Already stored symbols: " + stored_symbol.size());
							if (found_result != null) {
								System.out.println("Comparing " + found_result + " and " + m.group(0));
								updateResult = found_result.compareTo(m.group(0));
								System.out.println("Results to update: " + updateResult);
							}
							else
								updateResult = -1;
							if (stored_symbol.size() > 0) {
								if (updateResult == 0) {
									System.out.println("Updating result symbol.");
									fm.updateSymbol(stored_symbol.get(0).getId(),
											result_list);
								}
								/*else
									fm.updateSymbol(stored_symbol.get(0).getId(),
											(Integer)table.getValueAt(changed_row, Fields.Id.getValue()));*/
								symbols_to_update.add(stored_symbol.get(0).getId());
							} else {
								if (updateResult == 0)
									symb_id = fm.insertSymbol(m.group(0), result_list);
								else
									symb_id = fm.insertSymbol(m.group(0));
								symbols_to_update.add(symb_id);
							}
						}
						fm.updateFormula((Integer)table.getValueAt(changed_row, Fields.Id.getValue()),
								table.getValueAt(changed_row, Fields.TeX.getValue()).toString(),
								Integer.parseInt(table.getValueAt(changed_row, Fields.Page.getValue()).toString()),
								symbols_to_update);
						
						changed_rows.remove(row_number);
						changed_row = -1;
					}
					if (changed_rows.size() == 1)
						insertion_left = changed_rows.get(0) == (table.getRowCount()-1);
				}
			}
		});
		JMenuItem mi_del = new JMenuItem("Delete");
		mi_del.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if ((selected_row != -1) && (selected_row < table.getRowCount()-1)) {
					fm.deleteFormula((Integer)table.getValueAt(selected_row, Fields.Id.getValue()));
					selected_row = -1;
					m = init_data();
					table.setModel(new DefaultTableModel(m, m_columns));
					table.getModel().addTableModelListener(tmlRefresh);
				}
			}
		});
		m0.add(mi_ins);
		m0.add(mi_upd);
		m0.add(mi_del);
		m1.add(mi_refresh);
		menuBar.add(m0);
		menuBar.add(m1);
		frame.setJMenuBar(menuBar);
	}
}
