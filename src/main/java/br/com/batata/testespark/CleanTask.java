/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.batata.testespark;

import static br.com.batata.testespark.ChatBatata.uploadDir;
import java.io.File;
import java.io.IOException;
import java.util.TimerTask;
import java.util.logging.Level;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author r0g0
 */
public class CleanTask extends TimerTask{

    public CleanTask () {
        
    }

    @Override
    public void run() {
         try {
             FileUtils.cleanDirectory(new File(uploadDir.toURI()));
            System.out.println("Clean files");
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(ChatBatata.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
