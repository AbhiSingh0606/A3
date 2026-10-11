package nz.foodrescue;

/** Checks whether a donor is verified. */
public interface DonorVerifier {
    /** Returns true if the donor is verified. */
    boolean isVerified(String donorId);
}