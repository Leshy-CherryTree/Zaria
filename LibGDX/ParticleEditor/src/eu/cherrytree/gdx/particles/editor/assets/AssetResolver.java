/****************************************/
/* AssetResolver.java					*/
/* Created on: 03-08-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles.editor.assets;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import java.io.File;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class AssetResolver implements FileHandleResolver
{
	//--------------------------------------------------------------------------
	
	private String assetPath;

	//--------------------------------------------------------------------------
	
	public AssetResolver(String assetPath)
	{
		this.assetPath = assetPath;
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public FileHandle resolve(String path)
	{
		return new FileHandle(new File(assetPath + path));
	}

	//--------------------------------------------------------------------------
}
