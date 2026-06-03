package generador;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Image;

import com.fazecast.jSerialComm.SerialPort;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class FunctionGeneratorUI extends JFrame {

    private static final long serialVersionUID = 1L;

    // --- Colores ------------------------------------------------------------
    private static final Color COLOR_FONDO        = new Color(245, 245, 248);
    private static final Color COLOR_PANEL        = Color.WHITE;
    private static final Color COLOR_ACENTO       = new Color(83,  74,  183);
    private static final Color COLOR_ACENTO_BRD   = new Color(174, 169, 236);
    private static final Color COLOR_VERDE        = new Color(15,  110,  86);
    private static final Color COLOR_VERDE_BG     = new Color(234, 243, 222);
    private static final Color COLOR_VERDE_BRD    = new Color(99,  153,  34);
    private static final Color COLOR_ROJO         = new Color(153,  60,  29);
    private static final Color COLOR_ROJO_BG      = new Color(250, 236, 231);
    private static final Color COLOR_ROJO_BRD     = new Color(216,  90,  48);
    private static final Color COLOR_AMARILLO_FG  = new Color(120,  90,   0);
    private static final Color COLOR_AMARILLO_BG  = new Color(255, 249, 219);
    private static final Color COLOR_AMARILLO_BRD = new Color(200, 160,   0);
    private static final Color COLOR_TEXTO_SEC    = new Color(100, 100, 110);
    private static final Color COLOR_BORDE        = new Color(210, 210, 220);
    private static final Color COLOR_BORDE_BTN    = new Color(200, 200, 215);
    private static final Color COLOR_SERIAL_BG    = new Color(240, 240, 250);
    private static final Color COLOR_ONDA_SEL_BG  = new Color(238, 237, 254);
    private static final Color COLOR_ONDA_SEL_BRD = new Color(83,  74,  183);

    // --- Fuentes ------------------------------------------------------------
    private static final Font FONT_TITULO = new Font("SansSerif",  Font.BOLD,  11);
    private static final Font FONT_VALOR  = new Font("Monospaced", Font.BOLD,  16);
    private static final Font FONT_LABEL  = new Font("SansSerif",  Font.PLAIN, 12);
    private static final Font FONT_FINO   = new Font("SansSerif",  Font.PLAIN, 11);
    private static final Font FONT_ESTADO = new Font("SansSerif",  Font.BOLD,  12);

    // --- Componentes publicos: senal ----------------------------------------
    public JSpinner      spinnerFrecuencia;
    public JSlider       sliderFrecuencia;
    public JSpinner      spinnerAmplitud;
    public JSlider       sliderAmplitud;
    public JSpinner      spinnerOffset;
    public JSlider       sliderOffset;
    public JToggleButton btnSeno;
    public JToggleButton btnCuadra;
    public JToggleButton btnTriangulo;
    public JButton       btnIniciar;
    public JButton       btnDetener;
    public JLabel        lblEstado;
    public JLabel        lblImagenOnda;

    // Ajuste fino frecuencia
    public JButton btnFreqMM;
    public JButton btnFreqM;
    public JButton btnFreqP;
    public JButton btnFreqPP;

    // Ajuste fino amplitud
    public JButton btnAmpMM;
    public JButton btnAmpM;
    public JButton btnAmpP;
    public JButton btnAmpPP;

    // Ajuste fino offset
    public JButton btnOffMM;
    public JButton btnOffM;
    public JButton btnOffP;
    public JButton btnOffPP;
    public JButton btnOffReset;

    // --- Componentes publicos: serial ---------------------------------------
    public JComboBox<String> comboPuertoCOM;
    public JComboBox<String> comboBaudRate;
    public JButton           btnRefrescarPuertos;
    public JButton           btnConectar;
    public JButton           btnDesconectar;
    public JLabel            lblEstadoSerial;


    // --- Imagenes de vista previa -------------------------------------------
    private static final String IMG_SENOIDAL   = "imagenes/senoidal.png";
    private static final String IMG_CUADRADA   = "imagenes/cuadrada.png";
    private static final String IMG_TRIANGULAR = "imagenes/triangular.png";

    // --- Comunicacion FG-RTU -------------------------------------------------
    private SerialPort serialPort;

    private static final int FG_ID = 0x01;

    private static final int REG_FRECUENCIA_HIGH = 0x0001;
    private static final int REG_FRECUENCIA_LOW  = 0x0002;
    private static final int REG_AMPLITUD        = 0x0003;
    private static final int REG_OFFSET          = 0x0004;
    private static final int REG_ONDA            = 0x0005;
    private static final int REG_SALIDA          = 0x0006;

    private static final int FUNC_ESCRIBIR_UN_REGISTRO = 0x06;
    private static final int FUNC_ESCRIBIR_VARIOS      = 0x10;

    // Evita envios duplicados cuando un listener actualiza otro componente
    private boolean actualizandoControles = false;

    // ── Constructor ─────────────────────────────────────────────────────────
    public FunctionGeneratorUI() {

        setTitle("Generador de Funciones - Panel de Control");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 580));

        JPanel panelRaiz = new JPanel(new BorderLayout(0, 0));
        panelRaiz.setBackground(COLOR_FONDO);
        setContentPane(panelRaiz);

        // ════════════════════════════════════════════════════════════════════
        // BARRA SUPERIOR: titulo + panel serial
        // ════════════════════════════════════════════════════════════════════
        JPanel panelSuperior = new JPanel(new BorderLayout(0, 0));
        panelSuperior.setBackground(COLOR_PANEL);
        panelSuperior.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDE));
        panelRaiz.add(panelSuperior, BorderLayout.NORTH);

        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 10));
        panelTitulo.setBackground(COLOR_PANEL);
        JLabel lblTituloVentana = new JLabel("Generador de Funciones - Panel de Control");
        lblTituloVentana.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblTituloVentana.setForeground(new Color(50, 50, 60));
        panelTitulo.add(lblTituloVentana);
        panelSuperior.add(panelTitulo, BorderLayout.WEST);

        // Panel serial
        TitledBorder bordeSerial = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_ACENTO_BRD, 1, true),
                "  Comunicacion Serial  ");
        bordeSerial.setTitleFont(FONT_TITULO);
        bordeSerial.setTitleColor(COLOR_ACENTO);

        JPanel panelSerial = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        panelSerial.setBackground(COLOR_SERIAL_BG);
        panelSerial.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, COLOR_BORDE),
                BorderFactory.createCompoundBorder(bordeSerial, new EmptyBorder(0, 4, 2, 4))));
        panelSuperior.add(panelSerial, BorderLayout.EAST);

        JLabel lblPuertoCOM = new JLabel("Puerto:");
        lblPuertoCOM.setFont(FONT_LABEL);
        lblPuertoCOM.setForeground(COLOR_TEXTO_SEC);
        panelSerial.add(lblPuertoCOM);

        comboPuertoCOM = new JComboBox<String>();
        comboPuertoCOM.setFont(FONT_LABEL);
        comboPuertoCOM.setPreferredSize(new Dimension(180, 28));
        poblarPuertosCOM();
        panelSerial.add(comboPuertoCOM);

        JLabel lblBaudRate = new JLabel("Baud:");
        lblBaudRate.setFont(FONT_LABEL);
        lblBaudRate.setForeground(COLOR_TEXTO_SEC);
        panelSerial.add(lblBaudRate);

        comboBaudRate = new JComboBox<String>();
        comboBaudRate.addItem("9600");
        comboBaudRate.addItem("19200");
        comboBaudRate.addItem("38400");
        comboBaudRate.addItem("57600");
        comboBaudRate.addItem("115200");
        comboBaudRate.setSelectedItem("9600");
        comboBaudRate.setFont(FONT_LABEL);
        comboBaudRate.setPreferredSize(new Dimension(90, 28));
        panelSerial.add(comboBaudRate);

        btnRefrescarPuertos = new JButton("Refrescar");
        btnRefrescarPuertos.setFont(FONT_FINO);
        btnRefrescarPuertos.setForeground(COLOR_ACENTO);
        btnRefrescarPuertos.setBackground(new Color(238, 237, 254));
        btnRefrescarPuertos.setBorder(BorderFactory.createLineBorder(COLOR_ACENTO_BRD, 1, true));
        btnRefrescarPuertos.setFocusPainted(false);
        btnRefrescarPuertos.setPreferredSize(new Dimension(90, 28));
        panelSerial.add(btnRefrescarPuertos);

        btnConectar = new JButton("Conectar");
        btnConectar.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnConectar.setForeground(COLOR_VERDE);
        btnConectar.setBackground(COLOR_VERDE_BG);
        btnConectar.setBorder(BorderFactory.createLineBorder(COLOR_VERDE_BRD, 1, true));
        btnConectar.setFocusPainted(false);
        btnConectar.setPreferredSize(new Dimension(90, 28));
        panelSerial.add(btnConectar);

        btnDesconectar = new JButton("Desconectar");
        btnDesconectar.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnDesconectar.setForeground(COLOR_ROJO);
        btnDesconectar.setBackground(COLOR_ROJO_BG);
        btnDesconectar.setBorder(BorderFactory.createLineBorder(COLOR_ROJO_BRD, 1, true));
        btnDesconectar.setFocusPainted(false);
        btnDesconectar.setPreferredSize(new Dimension(110, 28));
        btnDesconectar.setEnabled(false);
        panelSerial.add(btnDesconectar);

        lblEstadoSerial = new JLabel("  Desconectado");
        lblEstadoSerial.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblEstadoSerial.setForeground(COLOR_ROJO);
        lblEstadoSerial.setBackground(COLOR_ROJO_BG);
        lblEstadoSerial.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_ROJO_BRD, 1, true),
                new EmptyBorder(2, 6, 2, 6)));
        lblEstadoSerial.setOpaque(true);
        lblEstadoSerial.setPreferredSize(new Dimension(160, 28));
        lblEstadoSerial.setHorizontalAlignment(SwingConstants.CENTER);
        panelSerial.add(lblEstadoSerial);

        // ════════════════════════════════════════════════════════════════════
        // PANEL CENTRAL
        // ════════════════════════════════════════════════════════════════════
        JPanel panelCentral = new JPanel();
        panelCentral.setBackground(COLOR_FONDO);
        panelCentral.setBorder(new EmptyBorder(8, 10, 8, 10));
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelRaiz.add(panelCentral, BorderLayout.CENTER);

        // ════════════════════════════════════════════════════════════════════
        // FILA 1: Forma de onda + Vista previa
        // ════════════════════════════════════════════════════════════════════
        JPanel panelFila1 = new JPanel(new GridLayout(1, 2, 10, 0));
        panelFila1.setOpaque(false);
        panelFila1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        // --- Forma de onda ---
        TitledBorder bordeFormaOnda = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_BORDE, 1, true), "  Forma de onda  ");
        bordeFormaOnda.setTitleFont(FONT_TITULO);
        bordeFormaOnda.setTitleColor(COLOR_TEXTO_SEC);

        JPanel panelFormaOnda = new JPanel(new GridBagLayout());
        panelFormaOnda.setBackground(COLOR_PANEL);
        panelFormaOnda.setBorder(BorderFactory.createCompoundBorder(
                bordeFormaOnda, new EmptyBorder(4, 8, 8, 8)));

        // Boton Senoidal  — icono: curva senoidal Unicode U+223F
        btnSeno = new JToggleButton(
                "<html><center>"
                + "<span style='font-family:Monospaced;font-size:20px;'> ╭╮╭╮</span><br>"
                + "<span style='font-family:Monospaced;font-size:20px;'>╰╯╰╯╰╯</span><br>"
                + "<span style='font-size:10px;'>Senoidal</span>"
                + "</center></html>");
        btnSeno.setFont(FONT_LABEL);
        btnSeno.setFocusPainted(false);
        btnSeno.setBackground(COLOR_PANEL);
        btnSeno.setForeground(COLOR_TEXTO_SEC);
        btnSeno.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 210), 1, true),
                new EmptyBorder(8, 4, 8, 4)));
        btnSeno.setSelected(true);
        btnSeno.setBackground(COLOR_ONDA_SEL_BG);
        btnSeno.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_ONDA_SEL_BRD, 2, true),
                new EmptyBorder(8, 4, 8, 4)));

        // Boton Cuadrada — icono: forma de onda cuadrada U+238B area / usamos SVG-like ASCII
        btnCuadra = new JToggleButton(
                "<html><center>"
                + "<span style='font-family:Monospaced;font-size:22px;'>&#9484;&#9472;&#9488;&nbsp;&#9484;&#9472;&#9488;</span><br>"
                + "<span style='font-family:Monospaced;font-size:22px;'>&#9496;&nbsp;&#9492;&#9472;&#9496;&nbsp;&#9492;</span><br>"
                + "<span style='font-size:10px;'>Cuadrada</span>"
                + "</center></html>");
        btnCuadra.setFont(FONT_LABEL);
        btnCuadra.setFocusPainted(false);
        btnCuadra.setBackground(COLOR_PANEL);
        btnCuadra.setForeground(COLOR_TEXTO_SEC);
        btnCuadra.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 210), 1, true),
                new EmptyBorder(8, 4, 8, 4)));

        // Boton Triangular — icono: triangulo arriba U+25B3
        btnTriangulo = new JToggleButton(
                "<html><center>"
                + "<span style='font-family:Monospaced;font-size:20px;'>╱╲╱╲╱╲</span><br>"
                + "<span style='font-size:10px;'>Triangular</span>"
                + "</center></html>");
        btnTriangulo.setFont(FONT_LABEL);
        btnTriangulo.setFocusPainted(false);
        btnTriangulo.setBackground(COLOR_PANEL);
        btnTriangulo.setForeground(COLOR_TEXTO_SEC);
        btnTriangulo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 210), 1, true),
                new EmptyBorder(8, 4, 8, 4)));

        GridBagConstraints gbcBtnSeno = new GridBagConstraints();
        gbcBtnSeno.gridx = 0; gbcBtnSeno.gridy = 0;
        gbcBtnSeno.fill = GridBagConstraints.BOTH;
        gbcBtnSeno.weightx = 1; gbcBtnSeno.weighty = 1;
        gbcBtnSeno.insets = new Insets(4, 4, 4, 4);
        panelFormaOnda.add(btnSeno, gbcBtnSeno);

        GridBagConstraints gbcBtnCuadra = new GridBagConstraints();
        gbcBtnCuadra.gridx = 1; gbcBtnCuadra.gridy = 0;
        gbcBtnCuadra.fill = GridBagConstraints.BOTH;
        gbcBtnCuadra.weightx = 1; gbcBtnCuadra.weighty = 1;
        gbcBtnCuadra.insets = new Insets(4, 4, 4, 4);
        panelFormaOnda.add(btnCuadra, gbcBtnCuadra);

        GridBagConstraints gbcBtnTriangulo = new GridBagConstraints();
        gbcBtnTriangulo.gridx = 2; gbcBtnTriangulo.gridy = 0;
        gbcBtnTriangulo.fill = GridBagConstraints.BOTH;
        gbcBtnTriangulo.weightx = 1; gbcBtnTriangulo.weighty = 1;
        gbcBtnTriangulo.insets = new Insets(4, 4, 4, 4);
        panelFormaOnda.add(btnTriangulo, gbcBtnTriangulo);

        panelFila1.add(panelFormaOnda);

        // --- Vista previa ---
        TitledBorder bordeVistaPrev = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_BORDE, 1, true), "  Vista previa de onda  ");
        bordeVistaPrev.setTitleFont(FONT_TITULO);
        bordeVistaPrev.setTitleColor(COLOR_TEXTO_SEC);

        JPanel panelVistaPrevia = new JPanel(new BorderLayout());
        panelVistaPrevia.setBackground(COLOR_PANEL);
        panelVistaPrevia.setBorder(BorderFactory.createCompoundBorder(
                bordeVistaPrev, new EmptyBorder(4, 8, 8, 8)));

        lblImagenOnda = new JLabel("Vista previa: Senoidal", SwingConstants.CENTER);
        lblImagenOnda.setFont(new Font("SansSerif", Font.ITALIC, 12));
        lblImagenOnda.setForeground(COLOR_TEXTO_SEC);
        lblImagenOnda.setBorder(BorderFactory.createDashedBorder(new Color(180, 180, 200), 4, 4));
        lblImagenOnda.setPreferredSize(new Dimension(0, 100));
        lblImagenOnda.setHorizontalAlignment(SwingConstants.CENTER);
        lblImagenOnda.setVerticalAlignment(SwingConstants.CENTER);
        // Para asignar imagen/GIF:
        //   lblImagenOnda.setIcon(new ImageIcon("imagenes/senoidal.gif"));
        //   lblImagenOnda.setText(null);
        panelVistaPrevia.add(lblImagenOnda, BorderLayout.CENTER);
        cargarImagenOnda(IMG_SENOIDAL, "Senoidal");
        panelFila1.add(panelVistaPrevia);
        panelCentral.add(panelFila1);

        // Espaciador
        JPanel espaciador1 = new JPanel();
        espaciador1.setOpaque(false);
        espaciador1.setPreferredSize(new Dimension(0, 8));
        espaciador1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 8));
        panelCentral.add(espaciador1);

        // ════════════════════════════════════════════════════════════════════
        // FILA 2: Frecuencia | Amplitud | Offset
        // ════════════════════════════════════════════════════════════════════
        JPanel panelFila2 = new JPanel(new GridLayout(1, 3, 10, 0));
        panelFila2.setOpaque(false);
        panelFila2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

        // ── FRECUENCIA ──────────────────────────────────────────────────────
        TitledBorder bordeFrecuencia = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_BORDE, 1, true), "  Frecuencia  ");
        bordeFrecuencia.setTitleFont(FONT_TITULO);
        bordeFrecuencia.setTitleColor(COLOR_TEXTO_SEC);
        JPanel panelFrecuencia = new JPanel(new GridBagLayout());
        panelFrecuencia.setBackground(COLOR_PANEL);
        panelFrecuencia.setBorder(BorderFactory.createCompoundBorder(
                bordeFrecuencia, new EmptyBorder(4, 8, 8, 8)));

        JLabel lblValorFrecuencia = new JLabel("Valor:");
        lblValorFrecuencia.setFont(FONT_LABEL);
        lblValorFrecuencia.setForeground(COLOR_TEXTO_SEC);
        GridBagConstraints gbcLblValorFrecuencia = new GridBagConstraints();
        gbcLblValorFrecuencia.gridx = 0; gbcLblValorFrecuencia.gridy = 0;
        gbcLblValorFrecuencia.anchor = GridBagConstraints.WEST;
        gbcLblValorFrecuencia.insets = new Insets(3, 3, 3, 3);
        panelFrecuencia.add(lblValorFrecuencia, gbcLblValorFrecuencia);

        spinnerFrecuencia = new JSpinner(new SpinnerNumberModel(1000.0, 1.0, 1000000.0, 1.0));
        spinnerFrecuencia.setFont(FONT_VALOR);
        ((JSpinner.DefaultEditor) spinnerFrecuencia.getEditor()).getTextField().setColumns(7);
        GridBagConstraints gbcSpinnerFrecuencia = new GridBagConstraints();
        gbcSpinnerFrecuencia.gridx = 1; gbcSpinnerFrecuencia.gridy = 0;
        gbcSpinnerFrecuencia.fill = GridBagConstraints.HORIZONTAL;
        gbcSpinnerFrecuencia.weightx = 1.0;
        gbcSpinnerFrecuencia.insets = new Insets(3, 3, 3, 3);
        panelFrecuencia.add(spinnerFrecuencia, gbcSpinnerFrecuencia);

        JLabel lblUnidadFrecuencia = new JLabel("Hz");
        lblUnidadFrecuencia.setFont(FONT_LABEL);
        lblUnidadFrecuencia.setForeground(COLOR_TEXTO_SEC);
        GridBagConstraints gbcLblHzFrecuencia = new GridBagConstraints();
        gbcLblHzFrecuencia.gridx = 2; gbcLblHzFrecuencia.gridy = 0;
        gbcLblHzFrecuencia.insets = new Insets(3, 3, 3, 3);
        panelFrecuencia.add(lblUnidadFrecuencia, gbcLblHzFrecuencia);

        JPanel panelBotonesFrecuencia = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 0));
        panelBotonesFrecuencia.setOpaque(false);
        btnFreqMM = new JButton("--");
        btnFreqMM.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnFreqMM.setPreferredSize(new Dimension(42, 28));
        btnFreqMM.setFocusPainted(false);
        btnFreqMM.setBackground(COLOR_PANEL);
        btnFreqMM.setBorder(BorderFactory.createLineBorder(COLOR_BORDE_BTN, 1, true));
        panelBotonesFrecuencia.add(btnFreqMM);
        btnFreqM = new JButton("-");
        btnFreqM.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnFreqM.setPreferredSize(new Dimension(42, 28));
        btnFreqM.setFocusPainted(false);
        btnFreqM.setBackground(COLOR_PANEL);
        btnFreqM.setBorder(BorderFactory.createLineBorder(COLOR_BORDE_BTN, 1, true));
        panelBotonesFrecuencia.add(btnFreqM);
        btnFreqP = new JButton("+");
        btnFreqP.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnFreqP.setPreferredSize(new Dimension(42, 28));
        btnFreqP.setFocusPainted(false);
        btnFreqP.setBackground(COLOR_PANEL);
        btnFreqP.setBorder(BorderFactory.createLineBorder(COLOR_BORDE_BTN, 1, true));
        panelBotonesFrecuencia.add(btnFreqP);
        btnFreqPP = new JButton("++");
        btnFreqPP.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnFreqPP.setPreferredSize(new Dimension(42, 28));
        btnFreqPP.setFocusPainted(false);
        btnFreqPP.setBackground(COLOR_PANEL);
        btnFreqPP.setBorder(BorderFactory.createLineBorder(COLOR_BORDE_BTN, 1, true));
        panelBotonesFrecuencia.add(btnFreqPP);
        GridBagConstraints gbcBotonesFrecuencia = new GridBagConstraints();
        gbcBotonesFrecuencia.gridx = 0; gbcBotonesFrecuencia.gridy = 1;
        gbcBotonesFrecuencia.gridwidth = 3;
        gbcBotonesFrecuencia.fill = GridBagConstraints.HORIZONTAL;
        gbcBotonesFrecuencia.insets = new Insets(3, 3, 3, 3);
        panelFrecuencia.add(panelBotonesFrecuencia, gbcBotonesFrecuencia);

        sliderFrecuencia = new JSlider(0, 1000, 100);
        sliderFrecuencia.setOpaque(false);
        GridBagConstraints gbcSliderFrecuencia = new GridBagConstraints();
        gbcSliderFrecuencia.gridx = 0; gbcSliderFrecuencia.gridy = 2;
        gbcSliderFrecuencia.gridwidth = 3;
        gbcSliderFrecuencia.fill = GridBagConstraints.HORIZONTAL;
        gbcSliderFrecuencia.insets = new Insets(3, 3, 3, 3);
        panelFrecuencia.add(sliderFrecuencia, gbcSliderFrecuencia);
        panelFila2.add(panelFrecuencia);

        // ── AMPLITUD ────────────────────────────────────────────────────────
        // Rango: 0.6 Vpp -- 20.0 Vpp
        TitledBorder bordeAmplitud = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_BORDE, 1, true), "  Amplitud  ");
        bordeAmplitud.setTitleFont(FONT_TITULO);
        bordeAmplitud.setTitleColor(COLOR_TEXTO_SEC);
        JPanel panelAmplitud = new JPanel(new GridBagLayout());
        panelAmplitud.setBackground(COLOR_PANEL);
        panelAmplitud.setBorder(BorderFactory.createCompoundBorder(
                bordeAmplitud, new EmptyBorder(4, 8, 8, 8)));

        JLabel lblValorAmplitud = new JLabel("Valor:");
        lblValorAmplitud.setFont(FONT_LABEL);
        lblValorAmplitud.setForeground(COLOR_TEXTO_SEC);
        GridBagConstraints gbcLblValorAmplitud = new GridBagConstraints();
        gbcLblValorAmplitud.gridx = 0; gbcLblValorAmplitud.gridy = 0;
        gbcLblValorAmplitud.anchor = GridBagConstraints.WEST;
        gbcLblValorAmplitud.insets = new Insets(3, 3, 3, 3);
        panelAmplitud.add(lblValorAmplitud, gbcLblValorAmplitud);

        spinnerAmplitud = new JSpinner(new SpinnerNumberModel(1.0, 0.6, 20.0, 0.01));
        spinnerAmplitud.setFont(FONT_VALOR);
        ((JSpinner.DefaultEditor) spinnerAmplitud.getEditor()).getTextField().setColumns(6);
        GridBagConstraints gbcSpinnerAmplitud = new GridBagConstraints();
        gbcSpinnerAmplitud.gridx = 1; gbcSpinnerAmplitud.gridy = 0;
        gbcSpinnerAmplitud.fill = GridBagConstraints.HORIZONTAL;
        gbcSpinnerAmplitud.weightx = 1.0;
        gbcSpinnerAmplitud.insets = new Insets(3, 3, 3, 3);
        panelAmplitud.add(spinnerAmplitud, gbcSpinnerAmplitud);

        JLabel lblUnidadAmplitud = new JLabel("Vpp");
        lblUnidadAmplitud.setFont(FONT_LABEL);
        lblUnidadAmplitud.setForeground(COLOR_TEXTO_SEC);
        GridBagConstraints gbcLblVppAmplitud = new GridBagConstraints();
        gbcLblVppAmplitud.gridx = 2; gbcLblVppAmplitud.gridy = 0;
        gbcLblVppAmplitud.insets = new Insets(3, 3, 3, 3);
        panelAmplitud.add(lblUnidadAmplitud, gbcLblVppAmplitud);

        JPanel panelBotonesAmplitud = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 0));
        panelBotonesAmplitud.setOpaque(false);
        btnAmpMM = new JButton("--");
        btnAmpMM.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnAmpMM.setPreferredSize(new Dimension(42, 28));
        btnAmpMM.setFocusPainted(false);
        btnAmpMM.setBackground(COLOR_PANEL);
        btnAmpMM.setBorder(BorderFactory.createLineBorder(COLOR_BORDE_BTN, 1, true));
        panelBotonesAmplitud.add(btnAmpMM);
        btnAmpM = new JButton("-");
        btnAmpM.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnAmpM.setPreferredSize(new Dimension(42, 28));
        btnAmpM.setFocusPainted(false);
        btnAmpM.setBackground(COLOR_PANEL);
        btnAmpM.setBorder(BorderFactory.createLineBorder(COLOR_BORDE_BTN, 1, true));
        panelBotonesAmplitud.add(btnAmpM);
        btnAmpP = new JButton("+");
        btnAmpP.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnAmpP.setPreferredSize(new Dimension(42, 28));
        btnAmpP.setFocusPainted(false);
        btnAmpP.setBackground(COLOR_PANEL);
        btnAmpP.setBorder(BorderFactory.createLineBorder(COLOR_BORDE_BTN, 1, true));
        panelBotonesAmplitud.add(btnAmpP);
        btnAmpPP = new JButton("++");
        btnAmpPP.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnAmpPP.setPreferredSize(new Dimension(42, 28));
        btnAmpPP.setFocusPainted(false);
        btnAmpPP.setBackground(COLOR_PANEL);
        btnAmpPP.setBorder(BorderFactory.createLineBorder(COLOR_BORDE_BTN, 1, true));
        panelBotonesAmplitud.add(btnAmpPP);
        GridBagConstraints gbcBotonesAmplitud = new GridBagConstraints();
        gbcBotonesAmplitud.gridx = 0; gbcBotonesAmplitud.gridy = 1;
        gbcBotonesAmplitud.gridwidth = 3;
        gbcBotonesAmplitud.fill = GridBagConstraints.HORIZONTAL;
        gbcBotonesAmplitud.insets = new Insets(3, 3, 3, 3);
        panelAmplitud.add(panelBotonesAmplitud, gbcBotonesAmplitud);

        // slider: 60 -- 2000 (representa 0.60 -- 20.00 V con factor /100)
        sliderAmplitud = new JSlider(60, 2000, 100);
        sliderAmplitud.setOpaque(false);
        GridBagConstraints gbcSliderAmplitud = new GridBagConstraints();
        gbcSliderAmplitud.gridx = 0; gbcSliderAmplitud.gridy = 2;
        gbcSliderAmplitud.gridwidth = 3;
        gbcSliderAmplitud.fill = GridBagConstraints.HORIZONTAL;
        gbcSliderAmplitud.insets = new Insets(3, 3, 3, 3);
        panelAmplitud.add(sliderAmplitud, gbcSliderAmplitud);
        panelFila2.add(panelAmplitud);

        // ── OFFSET ──────────────────────────────────────────────────────────
        // Rango: -5.0 V -- +5.0 V
        TitledBorder bordeOffset = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_BORDE, 1, true), "  Offset DC  ");
        bordeOffset.setTitleFont(FONT_TITULO);
        bordeOffset.setTitleColor(COLOR_TEXTO_SEC);
        JPanel panelOffset = new JPanel(new GridBagLayout());
        panelOffset.setBackground(COLOR_PANEL);
        panelOffset.setBorder(BorderFactory.createCompoundBorder(
                bordeOffset, new EmptyBorder(4, 8, 8, 8)));

        JLabel lblValorOffset = new JLabel("Offset:");
        lblValorOffset.setFont(FONT_LABEL);
        lblValorOffset.setForeground(COLOR_TEXTO_SEC);
        GridBagConstraints gbcLblValorOffset = new GridBagConstraints();
        gbcLblValorOffset.gridx = 0; gbcLblValorOffset.gridy = 0;
        gbcLblValorOffset.anchor = GridBagConstraints.WEST;
        gbcLblValorOffset.insets = new Insets(3, 3, 3, 3);
        panelOffset.add(lblValorOffset, gbcLblValorOffset);

        spinnerOffset = new JSpinner(new SpinnerNumberModel(0.0, -5.0, 5.0, 0.01));
        spinnerOffset.setFont(FONT_VALOR);
        ((JSpinner.DefaultEditor) spinnerOffset.getEditor()).getTextField().setColumns(6);
        GridBagConstraints gbcSpinnerOffset = new GridBagConstraints();
        gbcSpinnerOffset.gridx = 1; gbcSpinnerOffset.gridy = 0;
        gbcSpinnerOffset.fill = GridBagConstraints.HORIZONTAL;
        gbcSpinnerOffset.weightx = 1.0;
        gbcSpinnerOffset.insets = new Insets(3, 3, 3, 3);
        panelOffset.add(spinnerOffset, gbcSpinnerOffset);

        JLabel lblUnidadOffset = new JLabel("V");
        lblUnidadOffset.setFont(FONT_LABEL);
        lblUnidadOffset.setForeground(COLOR_TEXTO_SEC);
        GridBagConstraints gbcLblVOffset = new GridBagConstraints();
        gbcLblVOffset.gridx = 2; gbcLblVOffset.gridy = 0;
        gbcLblVOffset.insets = new Insets(3, 3, 3, 3);
        panelOffset.add(lblUnidadOffset, gbcLblVOffset);

        JPanel panelBotonesOffset = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 0));
        panelBotonesOffset.setOpaque(false);
        btnOffMM = new JButton("--");
        btnOffMM.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnOffMM.setPreferredSize(new Dimension(42, 28));
        btnOffMM.setFocusPainted(false);
        btnOffMM.setBackground(COLOR_PANEL);
        btnOffMM.setBorder(BorderFactory.createLineBorder(COLOR_BORDE_BTN, 1, true));
        panelBotonesOffset.add(btnOffMM);
        btnOffM = new JButton("-");
        btnOffM.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnOffM.setPreferredSize(new Dimension(42, 28));
        btnOffM.setFocusPainted(false);
        btnOffM.setBackground(COLOR_PANEL);
        btnOffM.setBorder(BorderFactory.createLineBorder(COLOR_BORDE_BTN, 1, true));
        panelBotonesOffset.add(btnOffM);
        btnOffP = new JButton("+");
        btnOffP.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnOffP.setPreferredSize(new Dimension(42, 28));
        btnOffP.setFocusPainted(false);
        btnOffP.setBackground(COLOR_PANEL);
        btnOffP.setBorder(BorderFactory.createLineBorder(COLOR_BORDE_BTN, 1, true));
        panelBotonesOffset.add(btnOffP);
        btnOffPP = new JButton("++");
        btnOffPP.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnOffPP.setPreferredSize(new Dimension(42, 28));
        btnOffPP.setFocusPainted(false);
        btnOffPP.setBackground(COLOR_PANEL);
        btnOffPP.setBorder(BorderFactory.createLineBorder(COLOR_BORDE_BTN, 1, true));
        panelBotonesOffset.add(btnOffPP);
        GridBagConstraints gbcBotonesOffset = new GridBagConstraints();
        gbcBotonesOffset.gridx = 0; gbcBotonesOffset.gridy = 1;
        gbcBotonesOffset.gridwidth = 3;
        gbcBotonesOffset.fill = GridBagConstraints.HORIZONTAL;
        gbcBotonesOffset.insets = new Insets(3, 3, 3, 3);
        panelOffset.add(panelBotonesOffset, gbcBotonesOffset);

        // slider: -500 -- 500 (representa -5.00 -- +5.00 V con factor /100)
        sliderOffset = new JSlider(-500, 500, 0);
        sliderOffset.setOpaque(false);
        GridBagConstraints gbcSliderOffset = new GridBagConstraints();
        gbcSliderOffset.gridx = 0; gbcSliderOffset.gridy = 2;
        gbcSliderOffset.gridwidth = 3;
        gbcSliderOffset.fill = GridBagConstraints.HORIZONTAL;
        gbcSliderOffset.insets = new Insets(3, 3, 3, 3);
        panelOffset.add(sliderOffset, gbcSliderOffset);

        btnOffReset = new JButton("Reset 0 V");
        btnOffReset.setFont(FONT_FINO);
        btnOffReset.setForeground(COLOR_VERDE);
        btnOffReset.setBackground(COLOR_VERDE_BG);
        btnOffReset.setBorder(BorderFactory.createLineBorder(COLOR_VERDE_BRD, 1, true));
        btnOffReset.setFocusPainted(false);
        GridBagConstraints gbcBtnOffReset = new GridBagConstraints();
        gbcBtnOffReset.gridx = 0; gbcBtnOffReset.gridy = 3;
        gbcBtnOffReset.gridwidth = 3;
        gbcBtnOffReset.fill = GridBagConstraints.HORIZONTAL;
        gbcBtnOffReset.insets = new Insets(3, 3, 3, 3);
        panelOffset.add(btnOffReset, gbcBtnOffReset);

        panelFila2.add(panelOffset);
        panelCentral.add(panelFila2);

        // Espaciador
        JPanel espaciador2 = new JPanel();
        espaciador2.setOpaque(false);
        espaciador2.setPreferredSize(new Dimension(0, 8));
        espaciador2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 8));
        panelCentral.add(espaciador2);

        // ════════════════════════════════════════════════════════════════════
        // FILA 3: Barra de estado + Iniciar / Detener
        // ════════════════════════════════════════════════════════════════════
        JPanel panelBarraEstado = new JPanel(new BorderLayout(10, 0));
        panelBarraEstado.setBackground(COLOR_PANEL);
        panelBarraEstado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE, 1, true),
                new EmptyBorder(6, 12, 6, 12)));
        panelBarraEstado.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

        lblEstado = new JLabel("Listo - sin salida activa");
        lblEstado.setFont(FONT_ESTADO);
        lblEstado.setForeground(COLOR_TEXTO_SEC);
        panelBarraEstado.add(lblEstado, BorderLayout.CENTER);

        JPanel panelBotonesControl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelBotonesControl.setOpaque(false);

        btnDetener = new JButton("Detener");
        btnDetener.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnDetener.setForeground(COLOR_ROJO);
        btnDetener.setBackground(COLOR_ROJO_BG);
        btnDetener.setBorder(BorderFactory.createLineBorder(COLOR_ROJO_BRD, 1, true));
        btnDetener.setFocusPainted(false);
        btnDetener.setPreferredSize(new Dimension(110, 32));
        btnDetener.setEnabled(false);
        panelBotonesControl.add(btnDetener);

        btnIniciar = new JButton("Iniciar");
        btnIniciar.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnIniciar.setForeground(COLOR_VERDE);
        btnIniciar.setBackground(COLOR_VERDE_BG);
        btnIniciar.setBorder(BorderFactory.createLineBorder(COLOR_VERDE_BRD, 1, true));
        btnIniciar.setFocusPainted(false);
        btnIniciar.setPreferredSize(new Dimension(110, 32));
        panelBotonesControl.add(btnIniciar);

        panelBarraEstado.add(panelBotonesControl, BorderLayout.EAST);
        panelCentral.add(panelBarraEstado);

        // ════════════════════════════════════════════════════════════════════
        // LISTENERS
        // ════════════════════════════════════════════════════════════════════

        // Serial: Conectar
        btnConectar.addActionListener(e -> {
            String puertoSel = (String) comboPuertoCOM.getSelectedItem();
            if (puertoSel == null || puertoSel.startsWith("--") || puertoSel.startsWith("(")) {
                lblEstadoSerial.setText("  Elige un puerto");
                lblEstadoSerial.setForeground(COLOR_AMARILLO_FG);
                lblEstadoSerial.setBackground(COLOR_AMARILLO_BG);
                lblEstadoSerial.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COLOR_AMARILLO_BRD, 1, true),
                        new EmptyBorder(2, 6, 2, 6)));
                return;
            }
            String nombrePuerto = puertoSel.split(" - ")[0];
            int baud = Integer.parseInt((String) comboBaudRate.getSelectedItem());

            serialPort = SerialPort.getCommPort(nombrePuerto);
            serialPort.setComPortParameters(baud, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
            serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 200, 200);

            if (!serialPort.openPort()) {
                lblEstadoSerial.setText("  Error al conectar");
                lblEstadoSerial.setForeground(COLOR_ROJO);
                lblEstadoSerial.setBackground(COLOR_ROJO_BG);
                lblEstadoSerial.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COLOR_ROJO_BRD, 1, true),
                        new EmptyBorder(2, 6, 2, 6)));
                serialPort = null;
                return;
            }

            btnConectar.setEnabled(false);
            btnDesconectar.setEnabled(true);
            comboPuertoCOM.setEnabled(false);
            comboBaudRate.setEnabled(false);
            btnRefrescarPuertos.setEnabled(false);
            lblEstadoSerial.setText("  Conectado: " + puertoSel.split(" - ")[0]);
            lblEstadoSerial.setForeground(COLOR_VERDE);
            lblEstadoSerial.setBackground(COLOR_VERDE_BG);
            lblEstadoSerial.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COLOR_VERDE_BRD, 1, true),
                    new EmptyBorder(2, 6, 2, 6)));

            // Sincroniza el microcontrolador con los valores actuales de la interfaz.
            enviarConfiguracionCompleta();
            enviarEstadoSalida(false);
        });

        // Serial: Desconectar
        btnDesconectar.addActionListener(e -> {
            if (serialPort != null && serialPort.isOpen()) {
                enviarEstadoSalida(false);
                serialPort.closePort();
            }
            serialPort = null;

            btnConectar.setEnabled(true);
            btnDesconectar.setEnabled(false);
            comboPuertoCOM.setEnabled(true);
            comboBaudRate.setEnabled(true);
            btnRefrescarPuertos.setEnabled(true);
            lblEstadoSerial.setText("  Desconectado");
            lblEstadoSerial.setForeground(COLOR_ROJO);
            lblEstadoSerial.setBackground(COLOR_ROJO_BG);
            lblEstadoSerial.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COLOR_ROJO_BRD, 1, true),
                    new EmptyBorder(2, 6, 2, 6)));
            if (!btnIniciar.isEnabled()) {
                btnIniciar.setEnabled(true);
                btnDetener.setEnabled(false);
                lblEstado.setForeground(COLOR_TEXTO_SEC);
                lblEstado.setText("Salida detenida - conexion serial cerrada");
            }
        });

        // Serial: Refrescar
        btnRefrescarPuertos.addActionListener(e -> poblarPuertosCOM());

        // Frecuencia: spinner -> slider (log)
        spinnerFrecuencia.addChangeListener(e -> {
            double v = ((Number) spinnerFrecuencia.getValue()).doubleValue();
            int pos = (int) Math.round((Math.log10(v) / 6.0) * 1000.0);
            actualizandoControles = true;
            sliderFrecuencia.setValue(Math.max(0, Math.min(1000, pos)));
            actualizandoControles = false;
            actualizarEstado();
            enviarFrecuenciaActual();
        });
        sliderFrecuencia.addChangeListener(e -> {
            if (actualizandoControles) return;
            if (sliderFrecuencia.getValueIsAdjusting()) {
                double hz = Math.pow(10.0, sliderFrecuencia.getValue() / 1000.0 * 6.0);
                spinnerFrecuencia.setValue(Math.round(hz * 10.0) / 10.0);
            }
        });

        // Amplitud: spinner -> slider (/100)
        spinnerAmplitud.addChangeListener(e -> {
            double v = ((Number) spinnerAmplitud.getValue()).doubleValue();
            actualizandoControles = true;
            sliderAmplitud.setValue((int) Math.round(v * 100.0));
            actualizandoControles = false;
            actualizarEstado();
            enviarAmplitudActual();
        });
        sliderAmplitud.addChangeListener(e -> {
            if (actualizandoControles) return;
            if (sliderAmplitud.getValueIsAdjusting())
                spinnerAmplitud.setValue(sliderAmplitud.getValue() / 100.0);
        });

        // Offset: spinner -> slider (/100)
        spinnerOffset.addChangeListener(e -> {
            double v = ((Number) spinnerOffset.getValue()).doubleValue();
            actualizandoControles = true;
            sliderOffset.setValue((int) Math.round(v * 100.0));
            actualizandoControles = false;
            actualizarEstado();
            enviarOffsetActual();
        });
        sliderOffset.addChangeListener(e -> {
            if (actualizandoControles) return;
            if (sliderOffset.getValueIsAdjusting())
                spinnerOffset.setValue(sliderOffset.getValue() / 100.0);
        });

        // Botones ajuste fino frecuencia (paso: 1 Hz para - y +, 10 Hz para -- y ++)
        btnFreqMM.addActionListener(e -> ajustarSpinner(spinnerFrecuencia, -10.0));
        btnFreqM .addActionListener(e -> ajustarSpinner(spinnerFrecuencia,  -1.0));
        btnFreqP .addActionListener(e -> ajustarSpinner(spinnerFrecuencia,   1.0));
        btnFreqPP.addActionListener(e -> ajustarSpinner(spinnerFrecuencia,  10.0));

        // Botones ajuste fino amplitud (paso: 0.1 V para - y +, 1.0 V para -- y ++)
        btnAmpMM.addActionListener(e -> ajustarSpinner(spinnerAmplitud, -1.0));
        btnAmpM .addActionListener(e -> ajustarSpinner(spinnerAmplitud, -0.1));
        btnAmpP .addActionListener(e -> ajustarSpinner(spinnerAmplitud,  0.1));
        btnAmpPP.addActionListener(e -> ajustarSpinner(spinnerAmplitud,  1.0));

        // Botones ajuste fino offset (paso: 0.1 V para - y +, 1.0 V para -- y ++)
        btnOffMM.addActionListener(e -> ajustarSpinner(spinnerOffset, -1.0));
        btnOffM .addActionListener(e -> ajustarSpinner(spinnerOffset, -0.1));
        btnOffP .addActionListener(e -> ajustarSpinner(spinnerOffset,  0.1));
        btnOffPP.addActionListener(e -> ajustarSpinner(spinnerOffset,  1.0));
        btnOffReset.addActionListener(e -> spinnerOffset.setValue(0.0));

        // Forma de onda
        btnSeno.addActionListener(e -> {
            btnSeno.setSelected(true);
            btnSeno.setBackground(COLOR_ONDA_SEL_BG);
            btnSeno.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COLOR_ONDA_SEL_BRD, 2, true),
                    new EmptyBorder(8, 4, 8, 4)));
            btnCuadra.setSelected(false);
            btnCuadra.setBackground(COLOR_PANEL);
            btnCuadra.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 210), 1, true),
                    new EmptyBorder(8, 4, 8, 4)));
            btnTriangulo.setSelected(false);
            btnTriangulo.setBackground(COLOR_PANEL);
            btnTriangulo.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 210), 1, true),
                    new EmptyBorder(8, 4, 8, 4)));
            cargarImagenOnda(IMG_SENOIDAL, "Senoidal");
            actualizarEstado();
            enviarOndaActual();
        });
        btnCuadra.addActionListener(e -> {
            btnSeno.setSelected(false);
            btnSeno.setBackground(COLOR_PANEL);
            btnSeno.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 210), 1, true),
                    new EmptyBorder(8, 4, 8, 4)));
            btnCuadra.setSelected(true);
            btnCuadra.setBackground(COLOR_ONDA_SEL_BG);
            btnCuadra.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COLOR_ONDA_SEL_BRD, 2, true),
                    new EmptyBorder(8, 4, 8, 4)));
            btnTriangulo.setSelected(false);
            btnTriangulo.setBackground(COLOR_PANEL);
            btnTriangulo.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 210), 1, true),
                    new EmptyBorder(8, 4, 8, 4)));
            cargarImagenOnda(IMG_CUADRADA, "Cuadrada");
            actualizarEstado();
            enviarOndaActual();
        });
        btnTriangulo.addActionListener(e -> {
            btnSeno.setSelected(false);
            btnSeno.setBackground(COLOR_PANEL);
            btnSeno.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 210), 1, true),
                    new EmptyBorder(8, 4, 8, 4)));
            btnCuadra.setSelected(false);
            btnCuadra.setBackground(COLOR_PANEL);
            btnCuadra.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 210), 1, true),
                    new EmptyBorder(8, 4, 8, 4)));
            btnTriangulo.setSelected(true);
            btnTriangulo.setBackground(COLOR_ONDA_SEL_BG);
            btnTriangulo.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COLOR_ONDA_SEL_BRD, 2, true),
                    new EmptyBorder(8, 4, 8, 4)));
            cargarImagenOnda(IMG_TRIANGULAR, "Triangular");
            actualizarEstado();
            enviarOndaActual();
        });

        // Iniciar
        btnIniciar.addActionListener(e -> {
            if (!serialConectado()) {
                lblEstado.setForeground(COLOR_ROJO);
                lblEstado.setText("Conecta un puerto serial antes de iniciar");
                return;
            }

            if (!enviarConfiguracionCompleta()) {
                lblEstado.setForeground(COLOR_ROJO);
                lblEstado.setText("No se pudo enviar la configuracion completa");
                return;
            }

            if (!enviarEstadoSalida(true)) {
                lblEstado.setForeground(COLOR_ROJO);
                lblEstado.setText("No se pudo activar la salida");
                return;
            }

            btnIniciar.setEnabled(false);
            btnDetener.setEnabled(true);
            lblEstado.setForeground(COLOR_VERDE);
            lblEstado.setText("Salida activa  |  " + textoEstadoActual());
        });

        // Detener
        btnDetener.addActionListener(e -> {
            enviarEstadoSalida(false);

            btnIniciar.setEnabled(true);
            btnDetener.setEnabled(false);
            lblEstado.setForeground(COLOR_TEXTO_SEC);
            lblEstado.setText("Salida detenida");
        });

        pack();
        setLocationRelativeTo(null);

        imprimirMapaRegistrosProtocolo();
    }

    // ── Metodos de soporte ──────────────────────────────────────────────────

    /**
     * Detecta puertos COM disponibles en el sistema usando jSerialComm.
     *
     * Para activar la deteccion real de puertos:
     *   1. Descarga jSerialComm: https://fazecast.github.io/jSerialComm/
     *   2. Eclipse > clic derecho proyecto > Build Path > Add External JARs
     *   3. Descomenta el bloque [jSerialComm] de abajo y elimina la lista de ejemplo.
     */
    private void poblarPuertosCOM() {
        comboPuertoCOM.removeAllItems();

        SerialPort[] puertos = SerialPort.getCommPorts();

        if (puertos.length == 0) {
            comboPuertoCOM.addItem("-- sin puertos detectados --");
            comboPuertoCOM.setSelectedIndex(0);
            return;
        }

        comboPuertoCOM.addItem("-- Seleccionar puerto --");

        for (SerialPort puerto : puertos) {
            comboPuertoCOM.addItem(
                    puerto.getSystemPortName() + " - " + puerto.getDescriptivePortName());
        }

        comboPuertoCOM.setSelectedIndex(0);
    }


    private boolean serialConectado() {
        return serialPort != null && serialPort.isOpen();
    }

    private boolean enviarFrecuenciaActual() {
        int frecuenciaHz = (int) Math.round(((Number) spinnerFrecuencia.getValue()).doubleValue());
        frecuenciaHz = Math.max(1, Math.min(1000000, frecuenciaHz));

        int freqHigh = (frecuenciaHz >> 16) & 0xFFFF;
        int freqLow  = frecuenciaHz & 0xFFFF;

        return escribirMultiplesRegistros(REG_FRECUENCIA_HIGH, new int[] { freqHigh, freqLow });
    }

    private boolean enviarAmplitudActual() {
        double amplitud = ((Number) spinnerAmplitud.getValue()).doubleValue();
        int valorRegistro = (int) Math.round(amplitud * 100.0);

        valorRegistro = Math.max(60, Math.min(2000, valorRegistro));

        return escribirRegistro(REG_AMPLITUD, valorRegistro);
    }

    private boolean enviarOffsetActual() {
        double offset = ((Number) spinnerOffset.getValue()).doubleValue();
        int valorRegistro = (int) Math.round(offset * 100.0);

        valorRegistro = Math.max(-500, Math.min(500, valorRegistro));

        return escribirRegistro(REG_OFFSET, valorRegistro);
    }

    private boolean enviarOndaActual() {
        return escribirRegistro(REG_ONDA, codigoOndaSeleccionada());
    }

    private boolean enviarEstadoSalida(boolean activar) {
        return escribirRegistro(REG_SALIDA, activar ? 1 : 0);
    }

    private boolean enviarConfiguracionCompleta() {
        int frecuenciaHz = (int) Math.round(((Number) spinnerFrecuencia.getValue()).doubleValue());
        frecuenciaHz = Math.max(1, Math.min(1000000, frecuenciaHz));

        int freqHigh = (frecuenciaHz >> 16) & 0xFFFF;
        int freqLow  = frecuenciaHz & 0xFFFF;

        int amplitud = (int) Math.round(((Number) spinnerAmplitud.getValue()).doubleValue() * 100.0);
        amplitud = Math.max(60, Math.min(2000, amplitud));

        int offset = (int) Math.round(((Number) spinnerOffset.getValue()).doubleValue() * 100.0);
        offset = Math.max(-500, Math.min(500, offset));

        int onda = codigoOndaSeleccionada();

        return escribirMultiplesRegistros(REG_FRECUENCIA_HIGH,
                new int[] { freqHigh, freqLow, amplitud, offset, onda });
    }

    private int codigoOndaSeleccionada() {
        if (btnSeno.isSelected())      return 0;
        if (btnCuadra.isSelected())    return 1;
        if (btnTriangulo.isSelected()) return 2;
        return 0;
    }

    private boolean escribirRegistro(int registro, int valor) {
        if (!serialConectado()) {
            imprimirEnvioCancelado(registro, valor);
            return false;
        }

        int valor16 = valor & 0xFFFF;

        byte[] trama = new byte[8];
        trama[0] = (byte) FG_ID;
        trama[1] = (byte) FUNC_ESCRIBIR_UN_REGISTRO;
        trama[2] = (byte) ((registro >> 8) & 0xFF);
        trama[3] = (byte) (registro & 0xFF);
        trama[4] = (byte) ((valor16 >> 8) & 0xFF);
        trama[5] = (byte) (valor16 & 0xFF);

        agregarCRC16Modbus(trama, 6);

        imprimirEnvioRegistro(registro, valor, trama);

        return enviarTrama(trama);
    }

    private boolean escribirMultiplesRegistros(int registroInicial, int[] valores) {
        if (!serialConectado()) {
            imprimirEnvioMultipleCancelado(registroInicial, valores);
            return false;
        }

        int cantidadRegistros = valores.length;
        int cantidadBytes = cantidadRegistros * 2;

        byte[] trama = new byte[9 + cantidadBytes];
        trama[0] = (byte) FG_ID;
        trama[1] = (byte) FUNC_ESCRIBIR_VARIOS;
        trama[2] = (byte) ((registroInicial >> 8) & 0xFF);
        trama[3] = (byte) (registroInicial & 0xFF);
        trama[4] = (byte) ((cantidadRegistros >> 8) & 0xFF);
        trama[5] = (byte) (cantidadRegistros & 0xFF);
        trama[6] = (byte) cantidadBytes;

        int indice = 7;
        for (int valor : valores) {
            int valor16 = valor & 0xFFFF;
            trama[indice++] = (byte) ((valor16 >> 8) & 0xFF);
            trama[indice++] = (byte) (valor16 & 0xFF);
        }

        agregarCRC16Modbus(trama, 7 + cantidadBytes);

        imprimirEnvioMultiplesRegistros(registroInicial, valores, trama);

        return enviarTrama(trama);
    }

    private void agregarCRC16Modbus(byte[] trama, int longitudSinCRC) {
        int crc = calcularCRC16Modbus(trama, longitudSinCRC);

        // En Modbus RTU el CRC se envia primero byte bajo y despues byte alto.
        trama[longitudSinCRC] = (byte) (crc & 0xFF);
        trama[longitudSinCRC + 1] = (byte) ((crc >> 8) & 0xFF);
    }

    private int calcularCRC16Modbus(byte[] datos, int longitud) {
        int crc = 0xFFFF;

        for (int i = 0; i < longitud; i++) {
            crc ^= datos[i] & 0xFF;

            for (int bit = 0; bit < 8; bit++) {
                if ((crc & 0x0001) != 0) {
                    crc = (crc >> 1) ^ 0xA001;
                } else {
                    crc = crc >> 1;
                }
            }
        }

        return crc & 0xFFFF;
    }

    private boolean enviarTrama(byte[] trama) {
        if (!serialConectado()) {
            System.out.println("[FG-RTU] No se envio la trama porque el puerto serial no esta conectado.");
            return false;
        }

        int bytesEnviados = serialPort.writeBytes(trama, trama.length);

        if (bytesEnviados != trama.length) {
            System.out.println("[FG-RTU] ERROR DE ENVIO | Bytes esperados: "
                    + trama.length + " | Bytes enviados: " + bytesEnviados);
            lblEstadoSerial.setText("  Error al enviar");
            lblEstadoSerial.setForeground(COLOR_ROJO);
            lblEstadoSerial.setBackground(COLOR_ROJO_BG);
            return false;
        }

        System.out.println("[FG-RTU] Trama enviada correctamente | Bytes: " + bytesEnviados);
        return true;
    }

    private void imprimirMapaRegistrosProtocolo() {
        System.out.println();
        System.out.println("============================================================");
        System.out.println("MAPA DE REGISTROS FG-RTU - GENERADOR DE FUNCIONES");
        System.out.println("============================================================");
        System.out.println("ID del dispositivo: 0x" + hex2(FG_ID));
        System.out.println("Formato trama 0x06: [ID][06][REG_H][REG_L][DATA_H][DATA_L][CRC_L][CRC_H]");
        System.out.println("Formato trama 0x10: [ID][10][REG_INI_H][REG_INI_L][NREG_H][NREG_L][NBYTES][DATOS...][CRC_L][CRC_H]");
        System.out.println("CRC: CRC16 Modbus, byte bajo primero y byte alto despues.");
        System.out.println();
        System.out.println("REGISTROS:");
        System.out.println("0x0001 | Frecuencia HIGH | uint16 | Parte alta de frecuencia de 32 bits");
        System.out.println("0x0002 | Frecuencia LOW  | uint16 | Parte baja de frecuencia de 32 bits");
        System.out.println("0x0003 | Amplitud        | uint16 | Vpp x 100 | rango 60 a 2000 = 0.60 a 20.00 Vpp");
        System.out.println("0x0004 | Offset DC       | int16  | V x 100   | rango -500 a 500 = -5.00 a +5.00 V");
        System.out.println("0x0005 | Tipo de onda    | uint16 | 0=Senoidal, 1=Cuadrada, 2=Triangular");
        System.out.println("0x0006 | Estado salida   | uint16 | 0=Apagada, 1=Activa");
        System.out.println();
        System.out.println("FUNCIONES:");
        System.out.println("0x06 | Escribir un registro");
        System.out.println("0x10 | Escribir multiples registros");
        System.out.println("============================================================");
        System.out.println();
    }

    private void imprimirEnvioRegistro(int registro, int valor, byte[] trama) {
        System.out.println();
        System.out.println("------------------------------------------------------------");
        System.out.println("[FG-RTU] ENVIO DE UN REGISTRO");
        System.out.println("Funcion: 0x06 - Escribir un registro");
        System.out.println("Registro: 0x" + hex4(registro) + " | " + nombreRegistro(registro));
        System.out.println("Valor decimal enviado: " + valor);
        System.out.println("Valor hexadecimal 16 bits: 0x" + hex4(valor & 0xFFFF));
        System.out.println("Interpretacion: " + interpretarValorRegistro(registro, valor));
        System.out.println("Trama TX: " + bytesToHex(trama));
        System.out.println("------------------------------------------------------------");
    }

    private void imprimirEnvioMultiplesRegistros(int registroInicial, int[] valores, byte[] trama) {
        System.out.println();
        System.out.println("------------------------------------------------------------");
        System.out.println("[FG-RTU] ENVIO DE MULTIPLES REGISTROS");
        System.out.println("Funcion: 0x10 - Escribir multiples registros");
        System.out.println("Registro inicial: 0x" + hex4(registroInicial));
        System.out.println("Cantidad de registros: " + valores.length);

        for (int i = 0; i < valores.length; i++) {
            int registro = registroInicial + i;
            int valor = valores[i];
            System.out.println("  -> Registro 0x" + hex4(registro)
                    + " | " + nombreRegistro(registro)
                    + " | Decimal: " + valor
                    + " | Hex: 0x" + hex4(valor & 0xFFFF)
                    + " | " + interpretarValorRegistro(registro, valor));
        }

        if (registroInicial == REG_FRECUENCIA_HIGH && valores.length >= 2) {
            int frecuencia32 = ((valores[0] & 0xFFFF) << 16) | (valores[1] & 0xFFFF);
            System.out.println("Frecuencia reconstruida: " + frecuencia32 + " Hz");
        }

        if (registroInicial == REG_FRECUENCIA_HIGH && valores.length >= 5) {
            System.out.println("Configuracion completa interpretada:");
            System.out.println("  Frecuencia: " + (((valores[0] & 0xFFFF) << 16) | (valores[1] & 0xFFFF)) + " Hz");
            System.out.println("  Amplitud: " + String.format("%.2f Vpp", (valores[2] & 0xFFFF) / 100.0));
            System.out.println("  Offset: " + String.format("%.2f V", convertirInt16(valores[3]) / 100.0));
            System.out.println("  Onda: " + nombreOnda(valores[4]));
        }

        System.out.println("Trama TX: " + bytesToHex(trama));
        System.out.println("------------------------------------------------------------");
    }

    private void imprimirEnvioCancelado(int registro, int valor) {
        System.out.println();
        System.out.println("[FG-RTU] ENVIO CANCELADO");
        System.out.println("Puerto serial no conectado.");
        System.out.println("Registro solicitado: 0x" + hex4(registro) + " | " + nombreRegistro(registro));
        System.out.println("Valor solicitado: " + valor + " | " + interpretarValorRegistro(registro, valor));
    }

    private void imprimirEnvioMultipleCancelado(int registroInicial, int[] valores) {
        System.out.println();
        System.out.println("[FG-RTU] ENVIO MULTIPLE CANCELADO");
        System.out.println("Puerto serial no conectado.");
        System.out.println("Registro inicial solicitado: 0x" + hex4(registroInicial));
        System.out.println("Cantidad de registros solicitada: " + valores.length);
    }

    private String nombreRegistro(int registro) {
        switch (registro) {
            case REG_FRECUENCIA_HIGH:
                return "Frecuencia HIGH";
            case REG_FRECUENCIA_LOW:
                return "Frecuencia LOW";
            case REG_AMPLITUD:
                return "Amplitud";
            case REG_OFFSET:
                return "Offset DC";
            case REG_ONDA:
                return "Tipo de onda";
            case REG_SALIDA:
                return "Estado de salida";
            default:
                return "Registro no definido";
        }
    }

    private String interpretarValorRegistro(int registro, int valor) {
        switch (registro) {
            case REG_FRECUENCIA_HIGH:
                return "Parte alta de la frecuencia de 32 bits.";
            case REG_FRECUENCIA_LOW:
                return "Parte baja de la frecuencia de 32 bits.";
            case REG_AMPLITUD:
                return String.format("Amplitud = %.2f Vpp", (valor & 0xFFFF) / 100.0);
            case REG_OFFSET:
                return String.format("Offset = %.2f V", convertirInt16(valor) / 100.0);
            case REG_ONDA:
                return "Onda = " + nombreOnda(valor);
            case REG_SALIDA:
                return ((valor & 0xFFFF) == 1) ? "Salida activa" : "Salida apagada";
            default:
                return "Sin interpretacion definida.";
        }
    }

    private int convertirInt16(int valor) {
        int valor16 = valor & 0xFFFF;
        if (valor16 >= 0x8000) {
            return valor16 - 0x10000;
        }
        return valor16;
    }

    private String nombreOnda(int valor) {
        switch (valor & 0xFFFF) {
            case 0:
                return "Senoidal";
            case 1:
                return "Cuadrada";
            case 2:
                return "Triangular";
            default:
                return "Desconocida";
        }
    }

    private String bytesToHex(byte[] datos) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < datos.length; i++) {
            if (i > 0) {
                sb.append(" ");
            }
            sb.append(hex2(datos[i] & 0xFF));
        }
        return sb.toString();
    }

    private String hex2(int valor) {
        return String.format("%02X", valor & 0xFF);
    }

    private String hex4(int valor) {
        return String.format("%04X", valor & 0xFFFF);
    }


    private void actualizarImagenOndaSeleccionada() {
        if (btnSeno.isSelected()) {
            cargarImagenOnda(IMG_SENOIDAL, "Senoidal");
        } else if (btnCuadra.isSelected()) {
            cargarImagenOnda(IMG_CUADRADA, "Cuadrada");
        } else if (btnTriangulo.isSelected()) {
            cargarImagenOnda(IMG_TRIANGULAR, "Triangular");
        }
    }

    /** Carga una imagen PNG en el panel de vista previa de onda. */
    private void cargarImagenOnda(String rutaImagen, String nombreOnda) {
        try {
            ImageIcon iconoOriginal = new ImageIcon(rutaImagen);

            if (iconoOriginal.getIconWidth() <= 0 || iconoOriginal.getIconHeight() <= 0) {
                lblImagenOnda.setIcon(null);
                lblImagenOnda.setText("No se encontro imagen: " + rutaImagen);
                return;
            }

            int ancho = lblImagenOnda.getWidth();
            int alto = lblImagenOnda.getHeight();

            if (ancho <= 0) {
                ancho = 360;
            }

            if (alto <= 0) {
                alto = 110;
            }

            Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(
                    ancho,
                    alto,
                    Image.SCALE_SMOOTH
            );

            lblImagenOnda.setIcon(new ImageIcon(imagenEscalada));
            lblImagenOnda.setText(null);
            lblImagenOnda.setToolTipText("Vista previa: " + nombreOnda);

        } catch (Exception ex) {
            lblImagenOnda.setIcon(null);
            lblImagenOnda.setText("Error al cargar imagen: " + nombreOnda);
            System.out.println("[UI] Error al cargar imagen de onda: " + ex.getMessage());
        }
    }

    /** Suma delta al valor del spinner respetando sus limites. */
    private void ajustarSpinner(JSpinner sp, double delta) {
        double actual = ((Number) sp.getValue()).doubleValue();
        SpinnerNumberModel model = (SpinnerNumberModel) sp.getModel();
        double min = ((Number) model.getMinimum()).doubleValue();
        double max = ((Number) model.getMaximum()).doubleValue();
        double nuevo = Math.min(max, Math.max(min, actual + delta));
        sp.setValue(Math.round(nuevo * 1000.0) / 1000.0);
    }

    private String ondaSeleccionada() {
        if (btnSeno.isSelected())      return "Senoidal";
        if (btnCuadra.isSelected())    return "Cuadrada";
        if (btnTriangulo.isSelected()) return "Triangular";
        return "---";
    }

    private String textoEstadoActual() {
        return String.format("%s  |  %.1f Hz  |  %.2f Vpp  |  Offset %.2f V",
                ondaSeleccionada(),
                ((Number) spinnerFrecuencia.getValue()).doubleValue(),
                ((Number) spinnerAmplitud.getValue()).doubleValue(),
                ((Number) spinnerOffset.getValue()).doubleValue());
    }

    private void actualizarEstado() {
        if (!btnIniciar.isEnabled())
            lblEstado.setText("Salida activa  |  " + textoEstadoActual());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new FunctionGeneratorUI().setVisible(true);
        });
    }
}