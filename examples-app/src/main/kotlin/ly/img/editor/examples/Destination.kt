package ly.img.editor.examples

sealed class Destination(
    val route: String,
    val title: String,
) {
    val deeplinkSchemes = listOf(
        "imgly://examples/$route",
    )

    data object Home : Destination(
        route = "home",
        title = "Home",
    )

    data object PhotoEditor : Destination(
        route = "starter-kit-photo-editor",
        title = "Photo Editor",
    )

    data object VideoEditor : Destination(
        route = "starter-kit-video-editor",
        title = "Video Editor",
    )

    data object MemoriesEditor : Destination(
        route = "starter-kit-memories-editor",
        title = "Memories Editor",
    )

    data object DesignEditor : Destination(
        route = "starter-kit-design-editor",
        title = "Design Editor",
    )

    data object PostcardEditor : Destination(
        route = "starter-kit-postcard-editor",
        title = "Postcard Editor",
    )

    data object ApparelEditor : Destination(
        route = "starter-kit-apparel-editor",
        title = "Apparel Editor",
    )

    data object AutoResizeGuide : Destination(
        route = "guide-auto-resize",
        title = "Auto-Resize",
    )

    data object DataMerge : Destination(
        route = "guides-data-merge",
        title = "Data Merge",
    )

    data object GuideColorPalette : Destination(
        route = "guide-color-palette",
        title = "Color Palette",
    )

    data object GuideCustomFonts : Destination(
        route = "guide-custom-fonts",
        title = "Custom Fonts",
    )

    data object GuideVariableFonts : Destination(
        route = "guide-variable-fonts",
        title = "Variable Fonts",
    )

    data object GuideCallbacks : Destination(
        route = "guide-callbacks",
        title = "Callbacks",
    )

    data object GuideUiEvents : Destination(
        route = "guide-ui-events",
        title = "UI Events",
    )

    data object GuideForceCrop : Destination(
        route = "guide-force-crop",
        title = "Force Crop",
    )

    data object GuideDock : Destination(
        route = "guide-dock",
        title = "Dock",
    )

    data object GuideTimeline : Destination(
        route = "guide-timeline",
        title = "Timeline",
    )

    data object GuideCropPresets : Destination(
        route = "guide-crop-presets",
        title = "Crop Presets",
    )

    data object GuidePageFormat : Destination(
        route = "guide-page-format",
        title = "Page Format",
    )

    data object GuideCustomPanel : Destination(
        route = "guide-custom-panel",
        title = "Custom Panel",
    )

    data object GuideCreateCustomPanel : Destination(
        route = "guide-create-custom-panel",
        title = "Create Custom Panel",
    )

    data object GuideTheming : Destination(
        route = "guide-theming",
        title = "Theming",
    )

    data object GuideIcons : Destination(
        route = "guide-icons",
        title = "Icons",
    )

    data object GuideOverlay : Destination(
        route = "guide-overlay",
        title = "Overlay",
    )

    data object GuideHideElements : Destination(
        route = "guide-hide-elements",
        title = "Hide Elements",
    )

    data object GuideForceTrim : Destination(
        route = "guide-force-trim",
        title = "Force Trim",
    )

    data object GuideRecordVoiceover : Destination(
        route = "guide-record-voiceover",
        title = "Record Voiceover",
    )

    data object GuideEditCaptions : Destination(
        route = "guide-edit-captions",
        title = "Edit Captions",
    )

    data object GuideAutoCaptions : Destination(
        route = "guide-auto-captions",
        title = "Auto Captions",
    )

    data object AutomateWorkflows : Destination(
        route = "guide-automate-workflows",
        title = "Automate Workflows",
    )

    data object GuideEditorState : Destination(
        route = "guide-editor-state",
        title = "Editor State",
    )

    data object GuideTemplating : Destination(
        route = "guide-templating",
        title = "Templating",
    )

    data object GuideDesignUnits : Destination(
        route = "guide-design-units",
        title = "Design Units",
    )

    data object GuideScenes : Destination(
        route = "guide-scenes",
        title = "Scenes",
    )

    data object GuideBuildYourOwnUI : Destination(
        route = "guide-build-your-own-ui",
        title = "Build Your Own UI",
    )

    data object GuideActions : Destination(
        route = "guide-actions",
        title = "Actions",
    )

    data object GuideAutomationActions : Destination(
        route = "guide-automation-actions",
        title = "Automation Actions",
    )

    data object GuideCustomizeBehaviour : Destination(
        route = "guide-customize-behaviour",
        title = "Customize Behaviour",
    )

    data object GuideRearrangeButtons : Destination(
        route = "guide-rearrange-buttons",
        title = "Rearrange Buttons",
    )

    data object GuideQuickActions : Destination(
        route = "guide-quick-actions",
        title = "Quick Actions",
    )

    data object GuideAddButton : Destination(
        route = "guide-add-button",
        title = "Add a New Button",
    )

    data object GuideCustomFeaturePlugin : Destination(
        route = "guide-custom-feature-plugin",
        title = "Custom Feature Plugin",
    )

    data object GuideBackgroundRemoval : Destination(
        route = "guide-background-removal",
        title = "Background Removal",
    )

    data object GuideAIImageGeneration : Destination(
        route = "guide-ai-image-generation",
        title = "AI Image Generation",
    )

    data object GuideAssetLibraryBasics : Destination(
        route = "guide-asset-library-basics",
        title = "Asset Library Basics",
    )

    data object GuideAssetLibrary : Destination(
        route = "guide-asset-library",
        title = "Asset Library",
    )

    data object GuidePhotoRoll : Destination(
        route = "guide-photo-roll",
        title = "Photo Roll",
    )

    data object GuideUserUpload : Destination(
        route = "guide-user-upload",
        title = "User Upload",
    )

    data object GuideAssetLibraryCustomize : Destination(
        route = "guide-asset-library-customize",
        title = "Asset Library Customize",
    )

    data object GuideCameraConfiguration : Destination(
        route = "guide-camera-configuration",
        title = "Camera Configuration",
    )

    companion object {
        val starterKits = listOf(
            PhotoEditor,
            VideoEditor,
            MemoriesEditor,
            DesignEditor,
            PostcardEditor,
            ApparelEditor,
        )

        val guides = listOf(
            AutoResizeGuide,
            DataMerge,
            GuideColorPalette,
            GuideCustomFonts,
            GuideVariableFonts,
            GuideCallbacks,
            GuideUiEvents,
            GuideForceCrop,
            GuideDock,
            GuideTimeline,
            GuideCropPresets,
            GuidePageFormat,
            GuideCustomPanel,
            GuideCreateCustomPanel,
            GuideTheming,
            GuideIcons,
            GuideOverlay,
            GuideHideElements,
            GuideForceTrim,
            GuideRecordVoiceover,
            GuideEditCaptions,
            GuideAutoCaptions,
            AutomateWorkflows,
            GuideEditorState,
            GuideTemplating,
            GuideDesignUnits,
            GuideScenes,
            GuideBuildYourOwnUI,
            GuideActions,
            GuideAutomationActions,
            GuideCustomizeBehaviour,
            GuideRearrangeButtons,
            GuideQuickActions,
            GuideAddButton,
            GuideCustomFeaturePlugin,
            GuideBackgroundRemoval,
            GuideAIImageGeneration,
            GuideAssetLibraryBasics,
            GuideAssetLibrary,
            GuidePhotoRoll,
            GuideUserUpload,
            GuideAssetLibraryCustomize,
            GuideCameraConfiguration,
        )
    }
}
