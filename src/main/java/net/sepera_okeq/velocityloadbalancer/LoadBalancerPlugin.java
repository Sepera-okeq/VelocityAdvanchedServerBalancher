package net.sepera_okeq.velocityloadbalancer;

import com.google.inject.Inject;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import de.leonhard.storage.Json;
import de.leonhard.storage.internal.FlatFile;

import java.nio.file.Path;
import java.util.*;

@Plugin(
        id = "velocityloadbalancer",
        name = "VelocityLoadBalancer",
        version = "1.0.0",
        url = "https://vk.com/sepera_okeq",
        description = "Advanced Velocity server balancer",
        authors = {
                "sepera_okeq / Sergey Leshkevich"
        }
)
public class LoadBalancerPlugin
{

    private final ProxyServer server;
    private final FlatFile config;
    private final HashMap<String, List<String>> servers = new HashMap<>();

    @Inject
    public LoadBalancerPlugin(ProxyServer server, @DataDirectory Path dataDirectory)
    {
        this.server = server;
        this.config = new Json("config", dataDirectory.toString());
    }

    /*  OLD CODE */
    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent e)
    {
        for (Map.Entry<String, Object> entry : this.config.getData().entrySet()) {
            this.servers.put(entry.getKey(), (List<String>) entry.getValue());
        }
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onLogin(PlayerChooseInitialServerEvent e) {
        if (e.getInitialServer().isPresent()) {
            RegisteredServer server = this.getServer(e.getInitialServer().get());

            if (server != null) {
                e.setInitialServer(server);
            }
        }
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onChangeServer(ServerPreConnectEvent e)
    {
        RegisteredServer server = this.getServer(e.getOriginalServer());

        if (server != null) {
            e.setResult(ServerPreConnectEvent.ServerResult.allowed(server));
        }
    }

    private RegisteredServer getServer(RegisteredServer registeredServer)
    {
        String key = null;

        for (String serverName : this.servers.keySet()) {
            if (registeredServer.getServerInfo().getName().startsWith(serverName)) {
                key = serverName;
                break;
            }
        }

        if (key == null) {
            return null;
        }

        Random rand = new Random();
        //List<String> servers = this.servers.get(key);
        Optional<RegisteredServer> server = this.server.matchServer(key).stream().min(Comparator.comparingInt(s -> s.getPlayersConnected().size()));
        return server.orElse(null);
    }
}
