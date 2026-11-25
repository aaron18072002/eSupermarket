import Link from "next/link";

export const StoreFooter = () => {
  return (
    <footer className="bg-[#FAF8F5] border-t border-[#EAE3DB] mt-20">
      <div
        className="max-w-7xl mx-auto px-4 sm:px-6 py-10 grid grid-cols-1
          sm:grid-cols-2 md:grid-cols-4 gap-6 text-xs"
      >
        <div className="space-y-1.5">
          <p className="font-semibold text-[#1F1916]">Authentic Selection</p>
          <p className="text-[#6E6259] leading-relaxed">
            Every product is sourced directly from authorized global brands.
          </p>
        </div>
        <div className="space-y-1.5">
          <p className="font-semibold text-[#1F1916]">30-Day Returns</p>
          <p className="text-[#6E6259] leading-relaxed">
            Seamless doorstep exchanges and returns within 30 days.
          </p>
        </div>
        <div className="space-y-1.5">
          <p className="font-semibold text-[#1F1916]">Same-Day Delivery</p>
          <p className="text-[#6E6259] leading-relaxed">
            Fast two-hour scheduled delivery across metropolitan areas.
          </p>
        </div>
        <div className="space-y-1.5">
          <p className="font-semibold text-[#1F1916]">Concierge Care</p>
          <p className="text-[#6E6259] leading-relaxed">
            Reach our product specialists daily at 1800 1061.
          </p>
        </div>
      </div>

      <div
        className="border-t border-[#EAE3DB] py-5 px-4 sm:px-6
          max-w-7xl mx-auto flex flex-col sm:flex-row items-center
          justify-between gap-2 text-[11px] text-[#6E6259]"
      >
        <p>© 2026 eSupermarket. All prices in VND.</p>
        <Link href="/login" className="text-[#3E2723] font-medium hover:underline">
          Member Account Sign In
        </Link>
      </div>
    </footer>
  );
};
