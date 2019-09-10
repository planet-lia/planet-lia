export function colorToNumber(color: string): number {
    return parseInt(color.substr(1, color.length), 16);
}

export function colorToString(color: number): string {
    return "#" + color.toString(16).toUpperCase();
}