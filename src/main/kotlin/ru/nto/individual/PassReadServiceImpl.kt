@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PassReadServiceImpl implements PassReadService {

    private final PassRepository passRepository;

    @Override
    public PassDetailsDto getById(Long passId) {
        Pass pass = passRepository.findById(passId)
                .orElseThrow(() -> new PassNotFoundException(passId));

        Visitor visitor = pass.getVisitor();

        VisitorDto visitorDto = new VisitorDto(
                visitor.getId(),
                visitor.getFullName(),
                visitor.getOrganization()  // null допустим
        );

        return new PassDetailsDto(
                pass.getId(),
                pass.getQrCode(),
                pass.getValidFrom(),
                pass.getValidTo(),
                pass.getMaxEntries(),
                pass.getRevokedAt(),       // null допустим
                visitorDto
        );
    }
}
