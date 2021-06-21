package com.easyparty;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.PartyChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.ws.PartyService;

import javax.inject.Inject;
import java.awt.image.BufferedImage;

@PluginDescriptor(
        name = "EasyParty",
        description = "Create a party without having to add people as a friend on Discord."
)
@Slf4j
public class EasyPartyPlugin extends Plugin {
    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    private PartyService partyService;

    private NavigationButton navigationButton;

    private EasyPartyPanel easyPartyPanel;

    @Override
    protected void startUp() throws Exception {
        final BufferedImage icon = ImageUtil.getResourceStreamFromClass(getClass(), "icon.png");
        easyPartyPanel = new EasyPartyPanel(partyService);

        navigationButton = NavigationButton.builder()
                .priority(9)
                .tooltip("EasyParty")
                .icon(icon)
                .panel(easyPartyPanel)
                .build();

        clientToolbar.addNavigation(navigationButton);
    }

    @Override
    protected void shutDown() throws Exception {
        clientToolbar.removeNavigation(navigationButton);
    }

    @Subscribe
    public void onPartyChanged(PartyChanged event) {
        // When a party gets created through the Party plugin, this will change the party shown in the EasyParty plugin
        easyPartyPanel.setPartyUUID(event.getPartyId());
    }
}
