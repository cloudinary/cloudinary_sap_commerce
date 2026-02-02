import {
    Component,
    HostListener,
    Output,
    EventEmitter,
    ChangeDetectorRef,
    Inject,
    Input,
    ViewChild,
    ElementRef
} from '@angular/core';
import {
    stringUtils,
    Media,
    MEDIA_FILE_SELECTOR_CUSTOM_INJECTOR_TOKEN,
    MediaFileSelectorCustomInjector,
    GenericEditorField
} from 'smarteditcommons';
import { CloudinaryScriptLoaderService } from '../services/CloudinaryScriptLoaderService';
import { CloudinaryConfigService } from '../services/CloudinaryConfigService';
import { CloudinaryAsset } from '../CloudinaryMediaFileFactoryUtil';
import { MediaUtilService } from '../services/MediaUtilService';

// Extend the global Window interface for the Cloudinary library
declare global {
    interface Window {
        cloudinary: any;
    }
}

@Component({
    selector: 'cloudinary-media-file-selector',
    templateUrl: './CloudinaryMediaFileSelectorComponent.html',
    styleUrls: ['./CloudinaryMediaFileSelectorComponent.scss'],
    standalone: false
})
export class CloudinaryMediaFileSelectorComponent {
    @Output() onMediaSaved: EventEmitter<Media> = new EventEmitter();
    @Input() acceptedFileTypes: string[];
    @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;

    public isUploading = false;
    public showPopup = false;
    public lastClicked: 'cloudinary' | 'local' | null = null;
    public field: GenericEditorField;

    constructor(
        private cloudinaryScriptLoader: CloudinaryScriptLoaderService,
        private cloudinaryConfigService: CloudinaryConfigService,
        private cdr: ChangeDetectorRef,
        @Inject(MEDIA_FILE_SELECTOR_CUSTOM_INJECTOR_TOKEN)
        private mediaFileSelectorCustomInjector: MediaFileSelectorCustomInjector,
        private mediaUtilService: MediaUtilService
    ) {}

    async ngOnInit(): Promise<void> {
        this.acceptedFileTypes = this.mediaUtilService.getAcceptedFileTypes(
            this.field.allowMediaType
        );
    }

    triggerFileInput(): void {
        this.fileInput.nativeElement.value = ''; // reset to allow re-selecting same file
        this.fileInput.nativeElement.click();
    }

    onCloudinaryClick(): void {
        this.isUploading = true;
        this.openCloudinaryWidget();
        this.showPopup = false;
        setTimeout(() => (this.isUploading = false), 2000);
    }

    /**
     * Handles the process of loading and opening the Cloudinary Media Library widget.
     */
    public async openCloudinaryWidget(): Promise<void> {
        this.isUploading = true;
        this.cdr.detectChanges();
        try {
            await this.cloudinaryScriptLoader.load();
            const config = await this.cloudinaryConfigService.getConfiguration();

            const widget = window.cloudinary.createMediaLibrary(
                {
                    cloud_name: config.cloudName,
                    api_key: config.apiKey,
                    multiple: false,
                    max_files: 1
                },
                {
                    insertHandler: (data: { assets: CloudinaryAsset[] }) => {
                        data.assets.forEach((asset) => {
                            this.handleAssetSelection(asset);
                        });
                        widget.hide();
                        this.isUploading = false;
                        this.cdr.detectChanges();
                    }
                }
            );
            widget.show();
        } catch (error) {
            console.error('Failed to open Cloudinary Media Library:', error);
            this.isUploading = false;
            this.cdr.detectChanges();
        }
    }

    togglePopup(): void {
        this.showPopup = !this.showPopup;
    }

    /**
     * Handles asset selection by creating a mock FileList and passing it
     * back to the parent OOTB component via the injected 'onSelect' callback.
     */
    private handleAssetSelection(asset: CloudinaryAsset): void {
        const mediaObject = {
            code: `${asset.public_id}.${asset.format}` + Date.now(),
            cloudinaryMediaJson: JSON.stringify(asset),
            // We add a custom property to identify it later, and mimic the 'name' property.
            name: `${asset.public_id}.${asset.format}`,
            type: 'application/json', // Set a MIME type for the Blob
            isCloudinary: true // A flag to identify our custom object
        };

        const jsonString = JSON.stringify(mediaObject);

        const mediaFile = new File([jsonString], mediaObject.name, {
            type: 'image/jpeg' // Set the MIME type
        });

        const mockFileList = {
            0: mediaFile,
            length: 1,
            item: (index: number) => (index === 0 ? mediaFile : null)
        };
        this.mediaFileSelectorCustomInjector.onSelect(mockFileList as any);
    }

    public buildAcceptedFileTypesList(): string {
        return this.acceptedFileTypes.map((fileType) => `.${fileType}`).join(',');
    }

    onSelect(event: Event): void {
        this.showPopup = false;
        const input = event.target as HTMLInputElement;
        if (input.files && input.files.length > 0) {
            this.lastClicked = 'local';
            this.mediaFileSelectorCustomInjector.onSelect(input.files);
        }
    }
}
