package dc.zona_fit;

import com.formdev.flatlaf.FlatDarculaLaf;
import dc.zona_fit.gui.ZonaFitForma;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;

@SpringBootApplication
public class ZonaFitSwing {
    static void main(String[] args) {
        //configuramos el modo oscuro
        FlatDarculaLaf.setup();

        //instanciar la fabrica de spring, se pone false ya que no es app web, si no de escritorio
        ConfigurableApplicationContext contextoSpring =
                new SpringApplicationBuilder(ZonaFitSwing.class)
                .headless(false)
                .web(WebApplicationType.NONE)
                .run(args); //pasamos argumentos del metodo main
        //crear un objeto de swing
        SwingUtilities.invokeLater(()->{
            ZonaFitForma zonaFitForma = contextoSpring.getBean(ZonaFitForma.class);
            zonaFitForma.setVisible(true);
        });//se espera a que todos los objetos de swing esten listos
    }
}
