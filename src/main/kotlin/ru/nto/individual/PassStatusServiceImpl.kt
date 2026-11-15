@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PassStatusServiceImpl implements PassStatusService {

    private final PassRepository passRepository;
    private final ScanRepository scanRepository;
    private final Clock clock;

    @Override
    public PassStatus status(Long passId) {
        Pass pass = passRepository.findById(passId)
                .orElseThrow(() -> new PassNotFoundException(passId));

        LocalDateTime now = LocalDateTime.now(clock); // один раз за вызов

        if (pass.getRevokedAt() != null) {
            return PassStatus.BLOCKED;
        }
        if (now.isBefore(pass.getValidFrom())) {
            return PassStatus.NOT_STARTED;
        }
        if (now.isAfter(pass.getValidTo())) {
            return PassStatus.EXPIRED;
        }

        long entries = scanRepository.countByPassIdAndDirectionAndScannedAtLessThanEqual(
                passId, ScanDirection.ENTRY, now);

        if (entries >= pass.getMaxEntries()) {
            return PassStatus.EXHAUSTED;
        }
        return PassStatus.ACTIVE;
    }
}
