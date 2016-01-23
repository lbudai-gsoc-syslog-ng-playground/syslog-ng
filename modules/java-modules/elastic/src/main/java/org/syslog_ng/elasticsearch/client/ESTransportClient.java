/*
 * Copyright (c) 2015 BalaBit IT Ltd, Budapest, Hungary
 * Copyright (c) 2015 Viktor Juhasz <viktor.juhasz@balabit.com>
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * As an additional exemption you are allowed to compile & link against the
 * OpenSSL libraries as published by the OpenSSL project. See the file
 * COPYING for details.
 *
 */

package org.syslog_ng.elasticsearch.client;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.ImmutableSettings.Builder;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.syslog_ng.elasticsearch.ElasticSearchOptions;

public class ESTransportClient extends ESClient {
	private Settings settings;
	private TransportClient transportClient;

	public ESTransportClient(ElasticSearchOptions options) {
		super(options);
	}

	private Settings buildSettings() {
		String clusterName = options.getCluster();
		String shieldCredentials = options.getShieldCredentials();
		String shieldKeystorePath = options.getShieldKeystorePath();
		String shieldKeystorePassword = options.getShieldKeystorePassword();
		String shieldTruststorePath = options.getShieldTruststorePath();
		String shieldTruststorePassword = options.getShieldTruststorePassword();
		Builder settingsBuilder = ImmutableSettings.settingsBuilder();
		settingsBuilder = settingsBuilder.put("client.transport.sniff", true).classLoader(Settings.class.getClassLoader());

		if (clusterName != null) {
			settingsBuilder = settingsBuilder.put("cluster.name", clusterName);
		}

		if (shieldCredentials != null) {
			settingsBuilder = settingsBuilder.put("shield.user", shieldCredentials);
		}
		
		if (shieldKeystorePath != null || shieldTruststorePath != null)
			settingsBuilder = settingsBuilder.put("shield.transport.ssl", "true");
		
		if (shieldKeystorePath != null) {
			settingsBuilder = settingsBuilder.put("shield.ssl.keystore.path", shieldKeystorePath);
			settingsBuilder = settingsBuilder.put("shield.ssl.keystore.password", shieldKeystorePassword);
		}

		if (shieldTruststorePath != null) {
			settingsBuilder = settingsBuilder.put("shield.ssl.truststore.path", shieldTruststorePath);
			settingsBuilder = settingsBuilder.put("shield.ssl.truststore.password", shieldTruststorePassword);
		}

		return settingsBuilder.build();
	}

	public Client createClient() {
		settings = buildSettings();

		String[] servers =  options.getServerList();

		transportClient = new TransportClient(settings);

		for (String server: servers) {
			transportClient.addTransportAddress(new InetSocketTransportAddress(server,
					options.getPort()));
		}
		return transportClient;
	}

	public void close() {
		getClient().close();
		resetClient();
	}

	@Override
	public boolean isOpened() {
		return !transportClient.connectedNodes().isEmpty();
	}

	@Override
	public void deinit() {

	}
}
