/*
 * Copyright (c) 2014 Pacnet and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.controller.aoniVnm;

/**
 *activator for vnm module
 *author: Yu Bingxin
 */

import org.opendaylight.controller.sal.binding.api.AbstractBindingAwareConsumer;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ConsumerContext;
import org.opendaylight.controller.sal.binding.api.NotificationService;
import org.opendaylight.yangtools.concepts.Registration;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Hashtable;

//import org.opendaylight.controller.sal.binding.api.BindingAwareBroker;
//old import

public class Activator extends AbstractBindingAwareConsumer implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(Activator.class);
    private AoniVnmHandler aoniVnmHandler;
    private ServiceRegistration<AoniVnmService> serviceRegistration;
    private NotificationService notificationService;
    private Registration vnmRegistration;
    // add actor


    @Override
    protected void startImpl(BundleContext context) {
        LOG.info("startImpl() passing");
        org.opendaylight.controller.aoniVnm.AoniVnmService aoniVnmService = new org.opendaylight.controller.aoniVnm.AoniVnmServiceImpl();
        serviceRegistration = context.registerService(AoniVnmService.class, aoniVnmService, new Hashtable<String, String>());
    }


    @Override
    public void onSessionInitialized(ConsumerContext session) {
        LOG.info("inSessionInitialized() passing");
        notificationService = session.getSALService(NotificationService.class);
        aoniVnmHandler = new AoniVnmHandler();
        //To send messages
       // SendPacket.packetPathModRequestService = session.getRpcService(PacketPathModRequestService.class);
        //SendPacket.packetErrorAckService = session.getRpcService(PacketErrorAckService.class);

        //To receive messages
        vnmRegistration = notificationService.registerNotificationListener(aoniVnmHandler);

    }
    @Override
    public void close() {
        LOG.info("close() passing");
    }
/*
    @Override
    protected void stopImpl(BundleContext context) {
        close();
        super.stopImpl(context);
    }
    */

}
