package views;

import common.ICamera;
import common.data.InputPackage;
import common.data.RendererData;

public interface IView {

    /**
     * Gives the active {@code Scene} object to the view to present it.
     */
    void giveData(ICamera cam, RendererData[] renderers);

    /**
     * Returns the {@code InputPackage} of the view
     *
     * @return {@code InputPackage}
     */
    InputPackage getInputPackage();


    void onResume();

    void onPause();

}