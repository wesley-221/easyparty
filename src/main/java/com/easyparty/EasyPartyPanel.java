package com.easyparty;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ws.PartyService;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class EasyPartyPanel extends PluginPanel {
    private static final String CREATE_BUTTON_TEXT = "Create party";
    private static final String JOIN_PARTY_TEXT = "Join party";

    private static final String CREATE_PARTY_SUCCESS = "Created a new party.";
    private static final String JOIN_PARTY_SUCCESS = "Joined the party.";

    private static final String EMPTY_PARTY_ID = "You have to enter a party id.";
    private static final String INVALID_PARTY_ID = "You entered an invalid party id.";

    private static final String COPY_SUCCESS = "Copied the party id to your clipboard.";
    private static final String NO_PARTY_JOINED = "No party joined.";

    private final JLabel currentPartyLabel = new JLabel(NO_PARTY_JOINED, SwingConstants.CENTER);
    private final JLabel messageLabel = new JLabel();
    private final JLabel copySuccessLabel = new JLabel();

    private final JTextField textFieldJoinParty = new JTextField();

    private UUID partyUUID;

    public EasyPartyPanel(PartyService partyService) {
        setBackground(ColorScheme.DARK_GRAY_COLOR);
        setLayout(new GridBagLayout());

        GridBagConstraints gridBagConstraints = new GridBagConstraints();

        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;

        gridBagConstraints.weightx = 1;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;

        gridBagConstraints.insets = new Insets(0, 0, 8, 0);

        JButton buttonCreateParty = new JButton(CREATE_BUTTON_TEXT);
        buttonCreateParty.addActionListener(e -> {
            setErrorLabel("");

            partyUUID = UUID.randomUUID();
            partyService.changeParty(partyUUID);

            currentPartyLabel.setText(String.valueOf(partyUUID));
            this.revalidate();
            this.repaint();

            setSuccessLabel(CREATE_PARTY_SUCCESS);
        });

        add(buttonCreateParty, gridBagConstraints);
        gridBagConstraints.gridy++;

        JButton buttonJoinParty = new JButton(JOIN_PARTY_TEXT);
        buttonJoinParty.addActionListener(e -> {
            setErrorLabel("");

            if (textFieldJoinParty.getText().isEmpty()) {
                setErrorLabel(EMPTY_PARTY_ID);
            } else {
                try {
                    partyUUID = UUID.fromString(textFieldJoinParty.getText().trim());
                    partyService.changeParty(partyUUID);

                    currentPartyLabel.setText(String.valueOf(partyUUID));
                    textFieldJoinParty.setText("");

                    this.revalidate();
                    this.repaint();

                    setSuccessLabel(JOIN_PARTY_SUCCESS);
                } catch (Exception ex) {
                    setErrorLabel(INVALID_PARTY_ID);
                }
            }
        });

        add(textFieldJoinParty, gridBagConstraints);
        gridBagConstraints.gridy++;

        add(buttonJoinParty, gridBagConstraints);
        gridBagConstraints.gridy++;

        add(messageLabel, gridBagConstraints);
        gridBagConstraints.gridy++;

        JPanel partyPanel = new JPanel();
        partyPanel.setLayout(new BoxLayout(partyPanel, BoxLayout.Y_AXIS));
        partyPanel.setBorder(new LineBorder(ColorScheme.DARKER_GRAY_COLOR));

        partyPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (partyUUID != null) {
                    StringSelection selection = new StringSelection(String.valueOf(partyUUID));
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(selection, selection);

                    setCopySuccessLabel();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                partyPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                partyPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        Border border = partyPanel.getBorder();
        Border margin = new EmptyBorder(10, 10, 10, 10);

        partyPanel.setBorder(new CompoundBorder(border, margin));

        JLabel copyLabel = new JLabel("Click to copy", SwingConstants.CENTER);
        copyLabel.setFont(new Font(FontManager.getRunescapeFont().getName(), Font.PLAIN, 25));
        copyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        currentPartyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        partyPanel.add(copyLabel);
        partyPanel.add(currentPartyLabel);
        add(partyPanel, gridBagConstraints);
        gridBagConstraints.gridy++;

        add(copySuccessLabel, gridBagConstraints);
        gridBagConstraints.gridy++;
    }

    /**
     * Set the value of partyUUID
     *
     * @param partyUUID the partyUUID to change to
     */
    public void setPartyUUID(UUID partyUUID) {
        if (partyUUID != null) {
            this.partyUUID = partyUUID;
            currentPartyLabel.setText(String.valueOf(partyUUID));
        } else {
            currentPartyLabel.setText(NO_PARTY_JOINED);
        }
    }

    /**
     * Set the text of the success label. Gets removed after a short delay
     *
     * @param text the text to change the label to
     */
    private void setSuccessLabel(String text) {
        messageLabel.setForeground(Color.GREEN);
        messageLabel.setText(text);

        resetMessageLabel();
    }

    /**
     * Set the text of the error label. Gets removed after a short delay
     *
     * @param text the text to change the label to
     */
    private void setErrorLabel(String text) {
        messageLabel.setForeground(Color.RED);
        messageLabel.setText(text);

        resetMessageLabel();
    }

    /**
     * Show a message when a user copies the party id
     */
    private void setCopySuccessLabel() {
        copySuccessLabel.setForeground(Color.GREEN);
        copySuccessLabel.setText(COPY_SUCCESS);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                copySuccessLabel.setText("");
            }
        }, 5000);
    }

    /**
     * Reset the message label after a short delay
     */
    private void resetMessageLabel() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                messageLabel.setText("");
            }
        }, 5000);
    }
}
