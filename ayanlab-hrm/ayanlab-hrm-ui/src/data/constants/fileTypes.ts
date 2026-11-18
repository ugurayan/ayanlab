import { 
    BsFiletypeDoc,
    BsFiletypeDocx,
    BsFiletypeJpg,
    BsFiletypeHeic,
    BsFiletypePdf,
    BsFiletypePng,
    BsFiletypePpt,
    BsFiletypePptx,
    BsFiletypePsd,
    BsFiletypeTxt, 
    BsFiletypeMp4,
    BsFiletypeXls,
    BsFiletypeXlsx,
    BsFileZip,
    BsFiletypeBmp,
    BsFiletypeGif,
    BsFiletypeSvg,
    BsFiletypeTiff
 } from "react-icons/bs";
 import { BiMoviePlay } from "react-icons/bi";


 export const DOCUMENT_UPLOAD_FILE_TYPE_MAP = {
    'doc':  { mime: 'application/msword',                                                        icon: BsFiletypeDoc,    color: '#F7DF1E' },
    'docx': { mime: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',   icon: BsFiletypeDocx,   color: '#F7DF1E' },
    'jpeg': { mime: 'image/jpeg',                                                                icon: BsFiletypeJpg,    color: '#F7DF1E' },
    'jpg':  { mime: 'image/jpeg',                                                                icon: BsFiletypeJpg,    color: '#F7DF1E' },
    'mkv':  { mime: 'video/x-matroska',                                                          icon: BiMoviePlay,      color: '#F7DF1E' },
    'mp4':  { mime: 'video/mp4',                                                                 icon: BsFiletypeMp4,    color: '#F7DF1E' },
    'mpeg': { mime: 'video/mpeg',                                                                icon: BiMoviePlay,      color: '#F7DF1E' },
    'pdf':  { mime: 'application/pdf',                                                           icon: BsFiletypePdf,    color: '#F7DF1E' },
    'png':  { mime: 'image/png',                                                                 icon: BsFiletypePng,    color: '#F7DF1E' },
    'ppt':  { mime: 'application/vnd.ms-powerpoint',                                             icon: BsFiletypePpt,    color: '#F7DF1E' },
    'pptx': { mime: 'application/vnd.openxmlformats-officedocument.presentationml.presentation', icon: BsFiletypePptx,   color: '#F7DF1E' },
    'psd':  { mime: 'image/vnd.adobe.photoshop',                                                 icon: BsFiletypePsd,    color: '#F7DF1E' },
    'tar':  { mime: 'application/x-tar',                                                         icon: BsFileZip,        color: '#F7DF1E' },
    'txt':  { mime: 'text/plain',                                                                icon: BsFiletypeTxt,    color: '#F7DF1E' },
    'xls':  { mime: 'application/vnd.ms-excel',                                                  icon: BsFiletypeXls,    color: '#F7DF1E' },
    'xlsx': { mime: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',         icon: BsFiletypeXlsx,   color: '#F7DF1E' },
    'zip':  { mime: 'application/zip',                                                           icon: BsFileZip,        color: '#F7DF1E' },
};

export const IMAGE_FILE_TYPE_MAP = {
    'bmp':  { mime: 'image/bmp',                                                                 icon: BsFiletypeBmp,    color: '#F7DF1E' },
    'gif':  { mime: 'image/gif',                                                                 icon: BsFiletypeGif,    color: '#F7DF1E' },
    'heic': { mime: 'image/heic',                                                                icon: BsFiletypeHeic,   color: '#F7DF1E' },
    'jpeg': { mime: 'image/jpeg',                                                                icon: BsFiletypeJpg,    color: '#F7DF1E' },
    'jpg':  { mime: 'image/jpeg',                                                                icon: BsFiletypeJpg,    color: '#F7DF1E' },
    'png':  { mime: 'image/png',                                                                 icon: BsFiletypePng,    color: '#F7DF1E' },
    'psd':  { mime: 'image/vnd.adobe.photoshop',                                                 icon: BsFiletypePsd,    color: '#F7DF1E' },
    'svg':  { mime: 'image/svg+xml',                                                             icon: BsFiletypeSvg,    color: '#F7DF1E' },
    'tiff': { mime: 'image/tiff',                                                                icon: BsFiletypeTiff,   color: '#F7DF1E' },
};