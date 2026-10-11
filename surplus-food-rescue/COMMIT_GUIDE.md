# Commit guide (delete before submitting if you like)

The files in this folder are the FINAL state. For the TDD marks, build the history
yourself in this order: type or paste each piece, run `mvn test`, commit.

0. chore: add project skeleton
   pom.xml, .gitignore, the 3 small types (FoodListing, DonorVerifier, ListingRepository),
   FoodListingService with the 3 checks and the validation calls removed
   (createListing just `return null;`).
1. test: valid listing  -> feat: `return listingRepository.save(new FoodListing(...))`
2. test: unverified donor (add UnauthorizedDonorException) -> feat: donor check
3. test: non-positive quantity (add InvalidListingException) -> feat: quantity check
4. test: short pickup window -> feat: window check
5. test: past expiry -> feat: expiry check
6. test: blank item name -> feat: item name check
7. test: boundary + order tests (pragmatic) -> refactor: extract private validators

Run `mvn test` before every commit. Do not squash or amend.
