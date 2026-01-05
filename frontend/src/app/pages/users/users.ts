import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { User, Usuario } from '../../core/services/user';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './users.html',
  styleUrl: './users.css',
})
export class Users implements OnInit {
  private userService = inject(User);
  usuarios$: Observable<Usuario[]> | undefined;

  ngOnInit() {
    this.usuarios$ = this.userService.getUsuarios();
  }

}
