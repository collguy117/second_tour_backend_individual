@Service
@RequiredArgsConstructor
public class PassRevokeServiceImpl implements PassRevokeService {

    private final PassRepository passRepository;
    private final Clock clock;

    @Override
    @Transactional
    public PassRevokeDto revoke(Long passId) {
        Pass pass = passRepository.findById(passId)
                .orElseThrow(() -> new PassNotFoundException(passId));

        if (pass.getRevokedAt() == null) {
            LocalDateTime revokedAt = LocalDateTime.now(clock)
                    .truncatedTo(ChronoUnit.SECONDS);
            pass.setRevokedAt(revokedAt);
            passRepository.save(pass);
        }

        return new PassRevokeDto(pass.getId(), pass.getRevokedAt());
    }
}
