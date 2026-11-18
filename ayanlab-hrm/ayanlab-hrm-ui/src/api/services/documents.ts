import { http } from '../client'; 
import { endpoints } from '../endpoints';
export async function apiListDocuments(){ 
    const res = await http.get(endpoints.documents.list);
    return res.data; 
}
