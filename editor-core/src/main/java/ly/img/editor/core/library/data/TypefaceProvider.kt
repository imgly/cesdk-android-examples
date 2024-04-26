package ly.img.editor.core.library.data

import ly.img.engine.Engine
import ly.img.engine.FindAssetsQuery
import ly.img.engine.Typeface

class TypefaceProvider {
    /**
     * A helper function for receiving [Typeface] payload of typeface asset that was previously added in [AssetSourceType.Typeface]
     * asset source.
     *
     * @param engine the engine to query.
     * @param name the name of the typeface.
     *
     * @return the typeface that was added in [AssetSourceType.Typeface] asset source.
     */
    suspend fun provideTypeface(
        engine: Engine,
        name: String,
    ): Typeface? {
        return engine.asset.findAssets(
            sourceId = AssetSourceType.Typeface.sourceId,
            query = FindAssetsQuery(perPage = 1, page = 0, query = name),
        )
            .assets
            .firstOrNull()
            ?.payload
            ?.typeface
    }
}
