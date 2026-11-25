/**
 * Formats numeric price values into Vietnamese Dong currency strings.
 */
export const formatVndCurrency = (amount: number): string => {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(amount);
};

/**
 * Formats package size and unit of measure into a concise specification label.
 */
export const formatPackageSpec = (
  packageSize: number | null,
  unitOfMeasure: string | null,
): string | null => {
  if (packageSize === null || !unitOfMeasure) {
    return null;
  }
  const cleanNumber = Number(packageSize);
  const formattedNumber = Number.isInteger(cleanNumber)
    ? String(cleanNumber)
    : String(Number(cleanNumber.toFixed(2)));
  return `${formattedNumber} ${unitOfMeasure}`;
};
