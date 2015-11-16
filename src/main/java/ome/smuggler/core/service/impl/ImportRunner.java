package ome.smuggler.core.service.impl;

import ome.smuggler.core.service.ImportProcessor;
import ome.smuggler.core.types.QueuedImport;

public class ImportRunner implements ImportProcessor {

    @Override
    public void consume(QueuedImport request) {
        // TODO Auto-generated method stub
        System.out.println("================= Queued Import =================");
        System.out.println(request.getTaskId().toString());
        System.out.println(request.getRequest().getExperimenterEmail());
        System.out.println(request.getRequest().getTarget());
        System.out.println(request.getRequest().getOmeroHost());
        System.out.println(request.getRequest().getSessionKey());
        System.out.println("=================================================");
    }

}
