using System;
using System.Collections.Generic;
using System.Text;
using Microsoft.AspNetCore.Identity.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore;
using SurveyServer.Models;

namespace SurveyServer.Data
{
    public class ApplicationDbContext : IdentityDbContext
    {
        public ApplicationDbContext(DbContextOptions<ApplicationDbContext> options)
            : base(options)
        {
        }
        public DbSet<SurveyServer.Models.Survey> Survey { get; set; }
        public DbSet<SurveyServer.Models.Response> Response { get; set; }
        public DbSet<SurveyServer.Models.Question> Question { get; set; }
        public DbSet<SurveyServer.Models.Answer> Answer { get; set; }
    }
}
