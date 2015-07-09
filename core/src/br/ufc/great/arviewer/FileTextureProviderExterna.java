package br.ufc.great.arviewer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.utils.TextureProvider;

/**
 * Created by carleandro on 30/06/15.
 *
 * @author Carleando Noleto
 */
public class FileTextureProviderExterna implements TextureProvider {

    @Override
    public Texture load(String fileName) {
        Texture result = new Texture(Gdx.files.external(fileName));
        result.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        result.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        return result;
    }
}
