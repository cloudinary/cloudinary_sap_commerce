// cloudinary-media-file-factory.util.ts

/**
 * This file contains utility functions for creating mock File and FileList objects
 * from Cloudinary asset data. This helps keep components clean and the logic reusable.
 */

// Re-defining the interface here for clarity, but it could also be
// moved to a shared types file (e.g., 'cloudinary.types.ts').
export interface CloudinaryAsset {
    public_id: string;
    format: string;
    resource_type: 'image' | 'video' | 'raw';
    type: string;
    bytes: number;
    display_name?: string;
}
