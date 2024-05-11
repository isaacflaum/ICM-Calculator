package icm;


public class IcmCalc {

    public static void main(String[] args) {

        IcmCalcView view = new IcmCalcView();

        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(view);
    }
}