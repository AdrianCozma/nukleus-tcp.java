/**
 * Copyright 2016-2017 The Reaktivity Project
 *
 * The Reaktivity Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.reaktivity.nukleus.tcp.internal;

import org.reaktivity.nukleus.Configuration;
import org.reaktivity.nukleus.Nukleus;
import org.reaktivity.nukleus.NukleusBuilder;
import org.reaktivity.nukleus.NukleusFactorySpi;
import org.reaktivity.nukleus.route.RouteKind;
import org.reaktivity.nukleus.tcp.internal.poller.Poller;
import org.reaktivity.nukleus.tcp.internal.stream.Acceptor;
import org.reaktivity.nukleus.tcp.internal.stream.ClientStreamFactoryBuilder;
import org.reaktivity.nukleus.tcp.internal.stream.ServerStreamFactoryBuilder;

public final class TcpNukleusFactorySpi implements NukleusFactorySpi
{
    public static final String NAME = "tcp";

    @Override
    public String name()
    {
        return NAME;
    }

    @Override
    public Nukleus create(
        Configuration config,
        NukleusBuilder builder)
    {
        Acceptor acceptor = new Acceptor();
        Poller poller = new Poller();
        acceptor.setPoller(poller);

        return builder.streamFactory(RouteKind.CLIENT, new ClientStreamFactoryBuilder(config, poller))
                      .streamFactory(RouteKind.SERVER, new ServerStreamFactoryBuilder(config, acceptor, poller))
                      .routeHandler(RouteKind.SERVER, acceptor::handleRoute)
                      .inject(poller)
                      .build();

    }
}
