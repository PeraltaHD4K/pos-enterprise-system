package com.diegoperalta.pos.modules.iam.application.ports;

import com.diegoperalta.pos.modules.iam.domain.Usuario;

public interface CurrentUserProvider {
    String getCurrentUsername();
    Usuario getCurrentUserDetails();
}
