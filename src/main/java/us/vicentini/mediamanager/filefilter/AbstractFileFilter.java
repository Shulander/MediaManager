/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.vicentini.mediamanager.filefilter;

import java.util.LinkedList;
import java.util.List;
import org.apache.commons.configuration.Configuration;

/**
 *
 * @author Shulander
 */
public abstract class AbstractFileFilter {
    protected List<String> fileFilterList;
    protected LinkedList<Object> fileExtensions;
    protected String destinationFolder;
    
    public void load(Configuration config, String section) {
        this.fileFilterList = new LinkedList<>();
        config.getList(section+".fileFilter").stream().forEach((fileFilter) -> {
            fileFilterList.add(fileFilter.toString());
        });
        
        this.fileExtensions = new LinkedList<>();
        config.getList(section+".fileextensions").stream().forEach((fileFilter) -> {
            fileExtensions.add(fileFilter.toString());
        });
        
        this.destinationFolder = config.getString(section+".destinationPath");
    }
    
}
