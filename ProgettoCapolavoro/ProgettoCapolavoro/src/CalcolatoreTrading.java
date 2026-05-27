import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * Classe principale con UI migliorata e palette colori Trading Moderno.
 * Segue le direttive: OOP, Swing/AWT, listener unico su classe Main.
 */
public class CalcolatoreTrading extends JFrame implements ActionListener {

    // --- PALETTE COLORI TRADING ---
    private final Color COLOR_BG = new Color(18, 22, 28);       // Sfondo molto scuro
    private final Color COLOR_PANEL = new Color(30, 35, 45);    // Pannelli interni
    private final Color COLOR_ACCENT = new Color(0, 122, 255);  // Blu Trading
    private final Color COLOR_TEXT = new Color(230, 230, 230);  // Testo quasi bianco
    private final Color COLOR_BORDER = new Color(50, 60, 75);   // Bordi discreti

    // Componenti
    private JComboBox<String> comboStrumento, comboValuta;
    private JTextField txtPipsStop, txtSaldo, txtRischio, txtPrezzo;
    private JButton btnCalcola;

    public CalcolatoreTrading() {
        // Configurazione Finestra
        setTitle("Calcolatore di dimensione posizione");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_BG);
        setLayout(new BorderLayout(15, 15));

        // Intestazione
        JLabel lblHeader = new JLabel("CALCOLATORE POSIZIONE", SwingConstants.CENTER);
        lblHeader.setForeground(COLOR_ACCENT);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeader.setBorder(new EmptyBorder(20, 0, 0, 0));
        add(lblHeader, BorderLayout.NORTH);

        // Pannello Input Centralizzato
        JPanel mainPanel = new JPanel(new GridLayout(3, 2, 25, 25));
        mainPanel.setBackground(COLOR_BG);
        mainPanel.setBorder(new EmptyBorder(30, 50, 30, 50));

        // Creazione campi seguendo il disegno
        mainPanel.add(creaCampoInput("Strumento", comboStrumento = new JComboBox<>(new String[]{"EUR/USD", "EUR/GBP", "GBP/USD", "XAU/USD"})));
        mainPanel.add(creaCampoInput("Valuta di versamento", comboValuta = new JComboBox<>(new String[]{"EUR", "USD", "GBP"})));
        mainPanel.add(creaCampoInput("Pips di stoploss", txtPipsStop = new JTextField()));
        mainPanel.add(creaCampoInput("Saldo del conto", txtSaldo = new JTextField()));
        mainPanel.add(creaCampoInput("Rischio %", txtRischio = new JTextField()));
        mainPanel.add(creaCampoInput("Prezzo Corrente", txtPrezzo = new JTextField("1.0000")));

        add(mainPanel, BorderLayout.CENTER);

        // Bottone Calcola
        btnCalcola = new JButton("CALCOLA");
        btnCalcola.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnCalcola.setBackground(COLOR_ACCENT);
        btnCalcola.setForeground(COLOR_ACCENT); // Testo bianco per contrasto sul blu
        btnCalcola.setFocusPainted(false);
        btnCalcola.setBorder(new LineBorder(COLOR_ACCENT, 1, true));
        btnCalcola.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCalcola.setPreferredSize(new Dimension(200, 50));
        btnCalcola.addActionListener(this);

        JPanel southPanel = new JPanel();
        southPanel.setBackground(COLOR_BG);
        southPanel.setBorder(new EmptyBorder(0, 0, 40, 0));
        southPanel.add(btnCalcola);
        add(southPanel, BorderLayout.SOUTH);
    }

    /**
     * Metodo helper per creare blocchi Label + Componente con stile uniforme
     */
    private JPanel creaCampoInput(String label, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBackground(COLOR_BG);

        JLabel l = new JLabel(label.toUpperCase());
        l.setForeground(new Color(150, 160, 180));
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        
        comp.setBackground(COLOR_PANEL);
        comp.setFont(new Font("Consolas", Font.PLAIN, 16));
        
        // --- LOGICA COLORE TESTO ---
        if (comp instanceof JComboBox) {
            // Imposta il testo della ComboBox in Blu Trading
            comp.setForeground(COLOR_ACCENT);
            // Renderer personalizzato per mantenere il blu anche nella tendina
            ((JComboBox<?>) comp).setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    setForeground(COLOR_ACCENT); // Testo Blu Trading
                    setBackground(isSelected ? new Color(50, 60, 75) : COLOR_PANEL);
                    return this;
                }
            });
        } else {
            // I JTextField rimangono con il colore di testo standard quasi bianco
            comp.setForeground(COLOR_TEXT);
        }

        ((JComponent)comp).setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(COLOR_BORDER, 1),
            new EmptyBorder(5, 10, 5, 10)
        ));

        p.add(l, BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    	String cmd = e.getActionCommand();
        if (cmd.equals("CALCOLA")) {
            try {
                String strumento = (String) comboStrumento.getSelectedItem();
                double pips = Double.parseDouble(txtPipsStop.getText());
                double saldo = Double.parseDouble(txtSaldo.getText());
                double rischioPerc = Double.parseDouble(txtRischio.getText());

                double patrimonioRischio = saldo * (rischioPerc / 100);
                double unitaPerLotto = strumento.contains("XAU") ? 100 : 100000;
                double valorePipPerLotto = 10.0; 
                
                double lotti = patrimonioRischio / (pips * valorePipPerLotto);
                double unitaTotal = lotti * unitaPerLotto;

                // Apre una nuova finestra con stile coerente
                new FinestraRisultato(lotti, unitaTotal, patrimonioRischio).setVisible(true);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Errore: Inserire dati numerici validi.");
            }
        }
    }

    /**
     * Finestra Output stilizzata
     */
    class FinestraRisultato extends JFrame {
        public FinestraRisultato(double lotti, double unita, double rischio) {
            setTitle("Risultati");
            setSize(350, 550);
            getContentPane().setBackground(COLOR_PANEL);
            setLayout(new GridLayout(3, 1, 10, 10));
            setLocationByPlatform(true);

            add(creaBoxRisultato("LOTTI", String.format("%.2f", lotti)));
            add(creaBoxRisultato("UNITÀ", String.format("%.0f", unita)));
            add(creaBoxRisultato("RISCHIO MONETARIO", String.format("%.2f €", rischio)));
        }

        private JPanel creaBoxRisultato(String tit, String val) {
            JPanel p = new JPanel(new GridBagLayout());
            p.setBackground(COLOR_PANEL);
            p.setBorder(new LineBorder(COLOR_BORDER, 1));
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0; gbc.gridy = 0;
            
            JLabel lTit = new JLabel(tit);
            lTit.setForeground(COLOR_ACCENT);
            lTit.setFont(new Font("Segoe UI", Font.BOLD, 12));
            p.add(lTit, gbc);

            gbc.gridy = 1;
            JLabel lVal = new JLabel(val);
            lVal.setForeground(Color.WHITE);
            lVal.setFont(new Font("Consolas", Font.BOLD, 28));
            p.add(lVal, gbc);

            return p;
        }
    }

    public static void main(String[] args) {
        // Applica un look and feel più moderno se possibile
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CalcolatoreTrading().setVisible(true);
            }
        });
    }
}