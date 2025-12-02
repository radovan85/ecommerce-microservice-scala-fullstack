export type SortDir = 'asc' | 'desc';


export function normalize(v: any): number | string {
    if (v == null) return '';
    if (typeof v === 'number') return v;
    if (typeof v === 'string') {
        const asDate = Date.parse(v);
        return isNaN(asDate) ? v.toLowerCase() : asDate;
    }
    return String(v).toLowerCase();
}


export function sortByKey<T extends Record<string, any>>(
    arr: T[],
    key: keyof T,
    dir: SortDir = 'asc'
): T[] {
    return [...arr].sort((a, b) => {
        const va = normalize(a[key]);
        const vb = normalize(b[key]);

        if (va < vb) return dir === 'asc' ? -1 : 1;
        if (va > vb) return dir === 'asc' ? 1 : -1;
        return 0;
    });
}